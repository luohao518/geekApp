package com.alibaba.fastjson.parser.deserializer;

import static com.alibaba.fastjson.util.ASMUtils.getDesc;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.asm.ASMException;
import com.alibaba.fastjson.asm.ClassWriter;
import com.alibaba.fastjson.asm.Label;
import com.alibaba.fastjson.asm.MethodVisitor;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.util.ASMClassLoader;
import com.alibaba.fastjson.util.ASMUtils;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.asm.FieldVisitor;
import com.alibaba.fastjson.asm.Opcodes;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.DeserializeBeanInfo;

public class ASMDeserializerFactory implements Opcodes {

    private static final ASMDeserializerFactory instance = new ASMDeserializerFactory();

    private final ASMClassLoader classLoader;

    private final AtomicLong                    seed     = new AtomicLong();

    public String getGenClassName(Class<?> clazz) {
        return "Fastjson_ASM_" + clazz.getSimpleName() + "_" + seed.incrementAndGet();
    }

    public String getGenFieldDeserializer(Class<?> clazz, FieldInfo fieldInfo) {
        String name = "Fastjson_ASM__Field_" + clazz.getSimpleName();
        name += "_" + fieldInfo.getName() + "_" + seed.incrementAndGet();

        return name;
    }

    public ASMDeserializerFactory(){
        classLoader = new ASMClassLoader();
    }

    public ASMDeserializerFactory(ClassLoader parentClassLoader){
        classLoader = new ASMClassLoader(parentClassLoader);
    }

    public final static ASMDeserializerFactory getInstance() {
        return instance;
    }

    public boolean isExternalClass(Class<?> clazz) {
        return classLoader.isExternalClass(clazz);
    }

    public ObjectDeserializer createJavaBeanDeserializer(ParserConfig config, Class<?> clazz, Type type)
                                                                                                        throws Exception {
        if (clazz.isPrimitive()) {
            throw new IllegalArgumentException("not support type :" + clazz.getName());
        }

        String className = getGenClassName(clazz);

        ClassWriter cw = new ClassWriter();
        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, className, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", null);

        DeserializeBeanInfo beanInfo = DeserializeBeanInfo.computeSetters(clazz, type);

        _init(cw, new Context(className, config, beanInfo, 3));
        _createInstance(cw, new Context(className, config, beanInfo, 3));
        _deserialze(cw, new Context(className, config, beanInfo, 4));
        _deserialzeArrayMapping(cw, new Context(className, config, beanInfo, 4));
        byte[] code = cw.toByteArray();

        if(JSON.DUMP_CLASS != null){
            FileOutputStream fos=null;
            try {
                fos=new FileOutputStream(JSON.DUMP_CLASS+ File.separator
                        + className + ".class");
                fos.write(code);
            }catch (Exception ex){
                System.err.println("FASTJSON dump class:"+className+"失败:"+ex.getMessage());
            }finally {
                if(fos!=null){
                    fos.close();
                }
            }
        }

        Class<?> exampleClass = classLoader.defineClassPublic(className, code, 0, code.length);

        Constructor<?> constructor = exampleClass.getConstructor(ParserConfig.class, Class.class);
        Object instance = constructor.newInstance(config, clazz);

        return (ObjectDeserializer) instance;
    }

    void _setFlag(MethodVisitor mw, Context context, int i) {
        String varName = "_asm_flag_" + (i / 32);

        mw.visitVarInsn(Opcodes.ILOAD, context.var(varName));
        mw.visitLdcInsn(1 << i);
        mw.visitInsn(Opcodes.IOR);
        mw.visitVarInsn(Opcodes.ISTORE, context.var(varName));
    }

    void _isFlag(MethodVisitor mw, Context context, int i, Label label) {
        mw.visitVarInsn(Opcodes.ILOAD, context.var("_asm_flag_" + (i / 32)));
        mw.visitLdcInsn(1 << i);
        mw.visitInsn(Opcodes.IAND);

        mw.visitJumpInsn(Opcodes.IFEQ, label);
    }

    void _deserialzeArrayMapping(ClassWriter cw, Context context) {
        MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "deserialzeArrayMapping"
                                          , "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;"
                                          , null, null);

        defineVarLexer(context, mw);

        _createInstance(context, mw);

        List<FieldInfo> sortedFieldInfoList = context.getBeanInfo().getSortedFieldList();
        int fieldListSize = sortedFieldInfoList.size();
        for (int i = 0; i < fieldListSize; ++i) {
            final boolean last = (i == fieldListSize - 1);
            final char seperator = last ? ']' : ',';

            FieldInfo fieldInfo = sortedFieldInfoList.get(i);
            Class<?> fieldClass = fieldInfo.getFieldClass();
            Type fieldType = fieldInfo.getFieldType();
            if (fieldClass == byte.class //
                || fieldClass == short.class //
                || fieldClass == int.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanInt", "(C)I");
                mw.visitVarInsn(Opcodes.ISTORE, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == long.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanLong", "(C)J");
                mw.visitVarInsn(Opcodes.LSTORE, context.var(fieldInfo.getName() + "_asm", 2));
            } else if (fieldClass == boolean.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanBoolean", "(C)Z");
                mw.visitVarInsn(Opcodes.ISTORE, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == float.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFloat", "(C)F");
                mw.visitVarInsn(Opcodes.FSTORE, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == double.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanDouble", "(C)D");
                mw.visitVarInsn(Opcodes.DSTORE, context.var(fieldInfo.getName() + "_asm", 2));
            } else if (fieldClass == char.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanString", "(C)Ljava/lang/String;");
                mw.visitInsn(Opcodes.ICONST_0);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C");
                mw.visitVarInsn(Opcodes.ISTORE, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == String.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanString", "(C)Ljava/lang/String;");
                mw.visitVarInsn(Opcodes.ASTORE, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass.isEnum()) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(fieldClass)));
                mw.visitVarInsn(Opcodes.ALOAD, 1);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getSymbolTable",
                                   "()Lcom/alibaba/fastjson/parser/SymbolTable;");
                mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanEnum",
                                   "(Ljava/lang/Class;Lcom/alibaba/fastjson/parser/SymbolTable;C)Ljava/lang/Enum;");
                mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(fieldClass)); // cast
                mw.visitVarInsn(Opcodes.ASTORE, context.var(fieldInfo.getName() + "_asm"));
            } else if (Collection.class.isAssignableFrom(fieldClass)) {
                Class<?> itemClass = getCollectionItemClass(fieldType);
                if (itemClass == String.class) {
                    mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                    mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(fieldClass)));
                    mw.visitVarInsn(Opcodes.BIPUSH, seperator);
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanStringArray",
                                       "(Ljava/lang/Class;C)Ljava/util/Collection;");
                    mw.visitVarInsn(Opcodes.ASTORE, context.var(fieldInfo.getName() + "_asm"));

                } else {
                    mw.visitVarInsn(Opcodes.ALOAD, 1);
                    if (i == 0) {
                        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "LBRACKET", "I");
                    } else {
                        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "COMMA", "I");
                    }
                    mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "LBRACKET", "I");
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "accept", "(II)V");

                    _newCollection(mw, fieldClass);
                    mw.visitInsn(Opcodes.DUP);
                    mw.visitVarInsn(Opcodes.ASTORE, context.var(fieldInfo.getName() + "_asm"));
                    _getCollectionFieldItemDeser(context, mw, fieldInfo, itemClass);
                    mw.visitVarInsn(Opcodes.ALOAD, 1);
                    mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(itemClass)));
                    mw.visitVarInsn(Opcodes.ALOAD, 3);
                    mw.visitMethodInsn(Opcodes.INVOKESTATIC, "com/alibaba/fastjson/util/ASMUtils", "parseArray",
                                       "(Ljava/util/Collection;" //
                                               + "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;" //
                                               + "Lcom/alibaba/fastjson/parser/DefaultJSONParser;" //
                                               + "Ljava/lang/reflect/Type;Ljava/lang/Object;)V");
                }

            } else {
                mw.visitVarInsn(Opcodes.ALOAD, 1);
                if (i == 0) {
                    mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "LBRACKET", "I");
                } else {
                    mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "COMMA", "I");
                }
                mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "LBRACKET", "I");
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "accept", "(II)V");

                _deserObject(context, mw, fieldInfo, fieldClass);

                mw.visitVarInsn(Opcodes.ALOAD, 1);
                if (!last) {
                    mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "COMMA", "I");
                    mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "LBRACKET", "I");
                } else {
                    mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "RBRACKET", "I");
                    mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "EOF", "I");
                }
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "accept", "(II)V");
                continue;
            }
        }

        _batchSet(context, mw, false);

        // lexer.nextToken(JSONToken.COMMA);
        mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "COMMA", "I");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "nextToken", "(I)V");

        mw.visitVarInsn(Opcodes.ALOAD, context.var("instance"));
        mw.visitInsn(Opcodes.ARETURN);
        mw.visitMaxs(5, context.getVariantCount());
        mw.visitEnd();
    }

    void _deserialze(ClassWriter cw, Context context) {
        if (context.getFieldInfoList().size() == 0) {
            return;
        }

        for (FieldInfo fieldInfo : context.getFieldInfoList()) {
            Class<?> fieldClass = fieldInfo.getFieldClass();
            Type fieldType = fieldInfo.getFieldType();

            if (fieldClass == char.class) {
                return;
            }

            if (Collection.class.isAssignableFrom(fieldClass)) {
                if (fieldType instanceof ParameterizedType) {
                    Type itemType = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
                    if (itemType instanceof Class) {
                        continue;
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
        }

        Collections.sort(context.getFieldInfoList());

        MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "deserialze"
                                          , "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;"
                                          , null, null);

        Label reset_ = new Label();
        Label super_ = new Label();
        Label return_ = new Label();
        Label end_ = new Label();

        defineVarLexer(context, mw);

        _isEnable(context, mw, Feature.SortFeidFastMatch);
        mw.visitJumpInsn(Opcodes.IFEQ, super_);

        {
            Label next_ = new Label();
            
            mw.visitVarInsn(Opcodes.ALOAD, 0);
            mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", "isSupportArrayToBean",
                               "(Lcom/alibaba/fastjson/parser/JSONLexer;)Z");
            mw.visitJumpInsn(Opcodes.IFEQ, next_);
            //isSupportArrayToBean
            
            mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "token", "()I");
            mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "LBRACKET", "I");
            mw.visitJumpInsn(Opcodes.IF_ICMPNE, next_);

            mw.visitVarInsn(Opcodes.ALOAD, 0);
            mw.visitVarInsn(Opcodes.ALOAD, 1);
            mw.visitVarInsn(Opcodes.ALOAD, 2);
            mw.visitVarInsn(Opcodes.ALOAD, 3);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, context.getClassName(), "deserialzeArrayMapping",
                               "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;");
            mw.visitInsn(Opcodes.ARETURN);

            mw.visitLabel(next_);
            // deserialzeArrayMapping
        }

        mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
        mw.visitLdcInsn(context.getClazz().getName());
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanType", "(Ljava/lang/String;)I");

        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONLexerBase", "NOT_MATCH", "I");
        mw.visitJumpInsn(Opcodes.IF_ICMPEQ, super_);

        mw.visitVarInsn(Opcodes.ALOAD, 1); // parser
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getContext",
                           "()Lcom/alibaba/fastjson/parser/ParseContext;");
        mw.visitVarInsn(Opcodes.ASTORE, context.var("mark_context"));

        // ParseContext context = parser.getContext();
        mw.visitInsn(Opcodes.ICONST_0);
        mw.visitVarInsn(Opcodes.ISTORE, context.var("matchedCount"));

        _createInstance(context, mw);

        {
            mw.visitVarInsn(Opcodes.ALOAD, 1); // parser
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getContext",
                               "()Lcom/alibaba/fastjson/parser/ParseContext;");
            mw.visitVarInsn(Opcodes.ASTORE, context.var("context"));

            mw.visitVarInsn(Opcodes.ALOAD, 1); // parser
            mw.visitVarInsn(Opcodes.ALOAD, context.var("context"));
            mw.visitVarInsn(Opcodes.ALOAD, context.var("instance"));
            mw.visitVarInsn(Opcodes.ALOAD, 3); // fieldName
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "setContext",
                               "(Lcom/alibaba/fastjson/parser/ParseContext;Ljava/lang/Object;Ljava/lang/Object;)Lcom/alibaba/fastjson/parser/ParseContext;");
            mw.visitVarInsn(Opcodes.ASTORE, context.var("childContext"));
        }

        mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
        mw.visitFieldInsn(Opcodes.GETFIELD, "com/alibaba/fastjson/parser/JSONLexerBase", "matchStat", "I");
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONLexerBase", "END", "I");
        mw.visitJumpInsn(Opcodes.IF_ICMPEQ, return_);

        mw.visitInsn(Opcodes.ICONST_0); // UNKOWN
        mw.visitIntInsn(Opcodes.ISTORE, context.var("matchStat"));

        int fieldListSize = context.getFieldInfoList().size();
        for (int i = 0; i < fieldListSize; i += 32) {
            mw.visitInsn(Opcodes.ICONST_0);
            mw.visitVarInsn(Opcodes.ISTORE, context.var("_asm_flag_" + (i / 32)));
        }

        // declare and init
        for (int i = 0; i < fieldListSize; ++i) {
            FieldInfo fieldInfo = context.getFieldInfoList().get(i);
            Class<?> fieldClass = fieldInfo.getFieldClass();

            if (fieldClass == boolean.class //
                || fieldClass == byte.class //
                || fieldClass == short.class //
                || fieldClass == int.class) {
                mw.visitInsn(Opcodes.ICONST_0);
                mw.visitVarInsn(Opcodes.ISTORE, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == long.class) {
                mw.visitInsn(Opcodes.LCONST_0);
                mw.visitVarInsn(Opcodes.LSTORE, context.var(fieldInfo.getName() + "_asm", 2));
            } else if (fieldClass == float.class) {
                mw.visitInsn(Opcodes.FCONST_0);
                mw.visitVarInsn(Opcodes.FSTORE, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == double.class) {
                mw.visitInsn(Opcodes.DCONST_0);
                mw.visitVarInsn(Opcodes.DSTORE, context.var(fieldInfo.getName() + "_asm", 2));
            } else {
                if (fieldClass == String.class) {
                    Label flagEnd_ = new Label();
                    _isEnable(context, mw, Feature.InitStringFieldAsEmpty);
                    mw.visitJumpInsn(Opcodes.IFEQ, flagEnd_);
                    _setFlag(mw, context, i);
                    mw.visitLabel(flagEnd_);

                    mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "stringDefaultValue",
                                       "()Ljava/lang/String;");
                } else {
                    mw.visitInsn(Opcodes.ACONST_NULL);
                }

                mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(fieldClass)); // cast
                mw.visitVarInsn(Opcodes.ASTORE, context.var(fieldInfo.getName() + "_asm"));
            }
        }

        for (int i = 0; i < fieldListSize; ++i) {
            FieldInfo fieldInfo = context.getFieldInfoList().get(i);
            Class<?> fieldClass = fieldInfo.getFieldClass();
            Type fieldType = fieldInfo.getFieldType();

            Label notMatch_ = new Label();

            if (fieldClass == boolean.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.ALOAD, 0);
                mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldBoolean", "([C)Z");
                mw.visitVarInsn(Opcodes.ISTORE, context.var(fieldInfo.getName() + "_asm"));
            } else if (fieldClass == byte.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.ALOAD, 0);
                mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldInt", "([C)I");
                mw.visitVarInsn(Opcodes.ISTORE, context.var(fieldInfo.getName() + "_asm"));

            } else if (fieldClass == short.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.ALOAD, 0);
                mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldInt", "([C)I");
                mw.visitVarInsn(Opcodes.ISTORE, context.var(fieldInfo.getName() + "_asm"));

            } else if (fieldClass == int.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.ALOAD, 0);
                mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldInt", "([C)I");
                mw.visitVarInsn(Opcodes.ISTORE, context.var(fieldInfo.getName() + "_asm"));

            } else if (fieldClass == long.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.ALOAD, 0);
                mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldLong", "([C)J");
                mw.visitVarInsn(Opcodes.LSTORE, context.var(fieldInfo.getName() + "_asm", 2));

            } else if (fieldClass == float.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.ALOAD, 0);
                mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldFloat", "([C)F");
                mw.visitVarInsn(Opcodes.FSTORE, context.var(fieldInfo.getName() + "_asm"));

            } else if (fieldClass == double.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.ALOAD, 0);
                mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldDouble", "([C)D");
                mw.visitVarInsn(Opcodes.DSTORE, context.var(fieldInfo.getName() + "_asm", 2));

            } else if (fieldClass == String.class) {
                Label notEnd_ = new Label();

                mw.visitIntInsn(Opcodes.ILOAD, context.var("matchStat"));
                mw.visitInsn(ICONST_4); // END
                mw.visitJumpInsn(Opcodes.IF_ICMPNE, notEnd_);

                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "stringDefaultValue",
                                   "()Ljava/lang/String;");
                mw.visitVarInsn(Opcodes.ASTORE, context.var(fieldInfo.getName() + "_asm"));
                mw.visitJumpInsn(Opcodes.GOTO, notMatch_);

                mw.visitLabel(notEnd_);

                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.ALOAD, 0);
                mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldString",
                                   "([C)Ljava/lang/String;");
                mw.visitVarInsn(Opcodes.ASTORE, context.var(fieldInfo.getName() + "_asm"));

            } else if (fieldClass.isEnum()) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.ALOAD, 0);
                mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");
                Label enumNull_ = new Label();
                mw.visitInsn(Opcodes.ACONST_NULL);
                mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(fieldClass)); // cast
                mw.visitVarInsn(Opcodes.ASTORE, context.var(fieldInfo.getName() + "_asm"));

                mw.visitVarInsn(Opcodes.ALOAD, 1);
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getSymbolTable",
                                   "()Lcom/alibaba/fastjson/parser/SymbolTable;");

                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldSymbol",
                                   "([CLcom/alibaba/fastjson/parser/SymbolTable;)Ljava/lang/String;");
                mw.visitInsn(Opcodes.DUP);
                mw.visitVarInsn(Opcodes.ASTORE, context.var(fieldInfo.getName() + "_asm_enumName"));

                mw.visitJumpInsn(Opcodes.IFNULL, enumNull_);
                mw.visitVarInsn(Opcodes.ALOAD, context.var(fieldInfo.getName() + "_asm_enumName"));
                mw.visitMethodInsn(Opcodes.INVOKESTATIC, ASMUtils.getType(fieldClass), "valueOf", "(Ljava/lang/String;)"
                                                                                 + ASMUtils.getDesc(fieldClass));
                mw.visitVarInsn(Opcodes.ASTORE, context.var(fieldInfo.getName() + "_asm"));
                mw.visitLabel(enumNull_);

            } else if (Collection.class.isAssignableFrom(fieldClass)) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitVarInsn(Opcodes.ALOAD, 0);
                mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");

                Class<?> itemClass = getCollectionItemClass(fieldType);

                if (itemClass == String.class) {
                    mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(fieldClass))); // cast
                    mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "scanFieldStringArray",
                                       "([CLjava/lang/Class;)" + ASMUtils.getDesc(Collection.class));
                    mw.visitVarInsn(Opcodes.ASTORE, context.var(fieldInfo.getName() + "_asm"));
                } else {
                    _deserialze_list_obj(context, mw, reset_, fieldInfo, fieldClass, itemClass, i);

                    if (i == fieldListSize - 1) {
                        _deserialize_endCheck(context, mw, reset_);
                    }
                    continue;
                }
            } else {
                _deserialze_obj(context, mw, reset_, fieldInfo, fieldClass, i);

                if (i == fieldListSize - 1) {
                    _deserialize_endCheck(context, mw, reset_);
                }

                continue;
            }

            mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
            mw.visitFieldInsn(Opcodes.GETFIELD, "com/alibaba/fastjson/parser/JSONLexerBase", "matchStat", "I");
            Label flag_ = new Label();
            // mw.visitInsn(DUP);
            mw.visitJumpInsn(Opcodes.IFLE, flag_);
            _setFlag(mw, context, i);
            mw.visitLabel(flag_);

            mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
            mw.visitFieldInsn(Opcodes.GETFIELD, "com/alibaba/fastjson/parser/JSONLexerBase", "matchStat", "I");
            mw.visitInsn(Opcodes.DUP);
            mw.visitVarInsn(Opcodes.ISTORE, context.var("matchStat"));

            mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONLexerBase", "NOT_MATCH", "I");
            mw.visitJumpInsn(Opcodes.IF_ICMPEQ, reset_);

            // mw.visitFieldInsn(GETSTATIC, getType(System.class), "out", "Ljava/io/PrintStream;");
            // mw.visitVarInsn(ALOAD, context.var("lexer"));
            // mw.visitFieldInsn(GETFIELD, "com/alibaba/fastjson/parser/JSONLexerBase", "matchStat", "I");
            // mw.visitMethodInsn(INVOKEVIRTUAL, getType(java.io.PrintStream.class), "println", "(I)V");

            mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
            mw.visitFieldInsn(Opcodes.GETFIELD, "com/alibaba/fastjson/parser/JSONLexerBase", "matchStat", "I");
            mw.visitJumpInsn(Opcodes.IFLE, notMatch_);

            // increment matchedCount
            mw.visitVarInsn(Opcodes.ILOAD, context.var("matchedCount"));
            mw.visitInsn(Opcodes.ICONST_1);
            mw.visitInsn(Opcodes.IADD);
            mw.visitVarInsn(Opcodes.ISTORE, context.var("matchedCount"));

            mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
            mw.visitFieldInsn(Opcodes.GETFIELD, "com/alibaba/fastjson/parser/JSONLexerBase", "matchStat", "I");
            mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONLexerBase", "END", "I");
            mw.visitJumpInsn(Opcodes.IF_ICMPEQ, end_);

            // mw.visitFieldInsn(GETSTATIC, getType(System.class), "out", "Ljava/io/PrintStream;");
            // mw.visitVarInsn(ILOAD, context.var("matchedCount"));
            // mw.visitMethodInsn(INVOKEVIRTUAL, getType(java.io.PrintStream.class), "println", "(I)V");

            mw.visitLabel(notMatch_);

            if (i == fieldListSize - 1) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
                mw.visitFieldInsn(Opcodes.GETFIELD, "com/alibaba/fastjson/parser/JSONLexerBase", "matchStat", "I");
                mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONLexerBase", "END", "I");
                mw.visitJumpInsn(Opcodes.IF_ICMPNE, reset_);
            }
        } // endFor

        mw.visitLabel(end_);

        if (!context.getClazz().isInterface() && !Modifier.isAbstract(context.getClazz().getModifiers())) {
            _batchSet(context, mw);
        }

        mw.visitLabel(return_);

        _setContext(context, mw);
        mw.visitVarInsn(Opcodes.ALOAD, context.var("instance"));
        mw.visitInsn(Opcodes.ARETURN);

        mw.visitLabel(reset_);

        _batchSet(context, mw);
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitVarInsn(Opcodes.ALOAD, 1);
        mw.visitVarInsn(Opcodes.ALOAD, 2);
        mw.visitVarInsn(Opcodes.ALOAD, 3);
        mw.visitVarInsn(Opcodes.ALOAD, context.var("instance"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", "parseRest",
                           "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(context.getClazz())); // cast
        mw.visitInsn(Opcodes.ARETURN);

        mw.visitLabel(super_);
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitVarInsn(Opcodes.ALOAD, 1);
        mw.visitVarInsn(Opcodes.ALOAD, 2);
        mw.visitVarInsn(Opcodes.ALOAD, 3);
        mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", "deserialze",
                           "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;");
        mw.visitInsn(Opcodes.ARETURN);

        mw.visitMaxs(5, context.getVariantCount());
        mw.visitEnd();
    }

    private Class<?> getCollectionItemClass(Type fieldType) {
        if (fieldType instanceof ParameterizedType) {
            Class<?> itemClass;
            Type actualTypeArgument = ((ParameterizedType) fieldType).getActualTypeArguments()[0];

            if (actualTypeArgument instanceof Class) {
                itemClass = (Class<?>) actualTypeArgument;
                if (!Modifier.isPublic(itemClass.getModifiers())) {
                    throw new ASMException("can not create ASMParser");
                }
            } else {
                throw new ASMException("can not create ASMParser");
            }
            return itemClass;
        }

        return Object.class;
    }

    private void _isEnable(Context context, MethodVisitor mw, Feature feature) {
        mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/Feature", feature.name(), "Lcom/alibaba/fastjson/parser/Feature;");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "isEnabled", "(Lcom/alibaba/fastjson/parser/Feature;)Z");
    }

    private void defineVarLexer(Context context, MethodVisitor mw) {
        mw.visitVarInsn(Opcodes.ALOAD, 1);
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getLexer", "()Lcom/alibaba/fastjson/parser/JSONLexer;");
        mw.visitTypeInsn(Opcodes.CHECKCAST, "com/alibaba/fastjson/parser/JSONLexerBase"); // cast
        mw.visitVarInsn(Opcodes.ASTORE, context.var("lexer"));
    }

    private void _createInstance(Context context, MethodVisitor mw) {
        Constructor<?> defaultConstructor = context.getBeanInfo().getDefaultConstructor();
        if (Modifier.isPublic(defaultConstructor.getModifiers())) {
            mw.visitTypeInsn(Opcodes.NEW, ASMUtils.getType(context.getClazz()));
            mw.visitInsn(Opcodes.DUP);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, ASMUtils.getType(context.getClazz()), "<init>", "()V");

            mw.visitVarInsn(Opcodes.ASTORE, context.var("instance"));
        } else {
            mw.visitVarInsn(Opcodes.ALOAD, 0);
            mw.visitVarInsn(Opcodes.ALOAD, 1);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", "createInstance",
                               "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;)Ljava/lang/Object;");
            mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(context.getClazz())); // cast
            mw.visitVarInsn(Opcodes.ASTORE, context.var("instance"));
        }
    }

    private void _batchSet(Context context, MethodVisitor mw) {
        _batchSet(context, mw, true);
    }

    private void _batchSet(Context context, MethodVisitor mw, boolean flag) {
        for (int i = 0, size = context.getFieldInfoList().size(); i < size; ++i) {
            Label notSet_ = new Label();

            if (flag) {
                _isFlag(mw, context, i, notSet_);
            }

            FieldInfo fieldInfo = context.getFieldInfoList().get(i);
            _loadAndSet(context, mw, fieldInfo);

            if (flag) {
                mw.visitLabel(notSet_);
            }
        }
    }

    private void _loadAndSet(Context context, MethodVisitor mw, FieldInfo fieldInfo) {
        Class<?> fieldClass = fieldInfo.getFieldClass();
        Type fieldType = fieldInfo.getFieldType();

        if (fieldClass == boolean.class) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("instance"));
            mw.visitVarInsn(Opcodes.ILOAD, context.var(fieldInfo.getName() + "_asm"));
            _set(context, mw, fieldInfo);
        } else if (fieldClass == byte.class //
                   || fieldClass == short.class //
                   || fieldClass == int.class //
                   || fieldClass == char.class) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("instance"));
            mw.visitVarInsn(Opcodes.ILOAD, context.var(fieldInfo.getName() + "_asm"));
            _set(context, mw, fieldInfo);
        } else if (fieldClass == long.class) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("instance"));
            mw.visitVarInsn(Opcodes.LLOAD, context.var(fieldInfo.getName() + "_asm", 2));
            if (fieldInfo.getMethod() != null) {
                mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, ASMUtils.getType(context.getClazz()), fieldInfo.getMethod().getName(),
                                   ASMUtils.getDesc(fieldInfo.getMethod()));
                if (!fieldInfo.getMethod().getReturnType().equals(Void.TYPE)) {
                    mw.visitInsn(Opcodes.POP);
                }
            } else {
                mw.visitFieldInsn(Opcodes.PUTFIELD, ASMUtils.getType(fieldInfo.getDeclaringClass()), fieldInfo.getField().getName(),
                                  ASMUtils.getDesc(fieldInfo.getFieldClass()));
            }
        } else if (fieldClass == float.class) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("instance"));
            mw.visitVarInsn(Opcodes.FLOAD, context.var(fieldInfo.getName() + "_asm"));
            _set(context, mw, fieldInfo);
        } else if (fieldClass == double.class) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("instance"));
            mw.visitVarInsn(Opcodes.DLOAD, context.var(fieldInfo.getName() + "_asm", 2));
            _set(context, mw, fieldInfo);
        } else if (fieldClass == String.class) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("instance"));
            mw.visitVarInsn(Opcodes.ALOAD, context.var(fieldInfo.getName() + "_asm"));
            _set(context, mw, fieldInfo);
        } else if (fieldClass.isEnum()) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("instance"));
            mw.visitVarInsn(Opcodes.ALOAD, context.var(fieldInfo.getName() + "_asm"));
            _set(context, mw, fieldInfo);
        } else if (Collection.class.isAssignableFrom(fieldClass)) {
            mw.visitVarInsn(Opcodes.ALOAD, context.var("instance"));
            Type itemType = getCollectionItemClass(fieldType);
            if (itemType == String.class) {
                mw.visitVarInsn(Opcodes.ALOAD, context.var(fieldInfo.getName() + "_asm"));
                mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(fieldClass)); // cast
            } else {
                mw.visitVarInsn(Opcodes.ALOAD, context.var(fieldInfo.getName() + "_asm"));
            }
            _set(context, mw, fieldInfo);

        } else {
            // mw.visitFieldInsn(GETSTATIC, getType(System.class), "out", "Ljava/io/PrintStream;");
            // mw.visitIntInsn(ILOAD, context.var(fieldInfo.getName() + "_asm_flag"));
            // mw.visitMethodInsn(INVOKEVIRTUAL, getType(java.io.PrintStream.class), "println", "(I)V");

            // _isFlag(mw, context, i, notSet_);

            mw.visitVarInsn(Opcodes.ALOAD, context.var("instance"));
            mw.visitVarInsn(Opcodes.ALOAD, context.var(fieldInfo.getName() + "_asm"));
            _set(context, mw, fieldInfo);

        }
    }

    private void _set(Context context, MethodVisitor mw, FieldInfo fieldInfo) {
        if (fieldInfo.getMethod() != null) {
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, ASMUtils.getType(fieldInfo.getDeclaringClass()), fieldInfo.getMethod().getName(),
                               ASMUtils.getDesc(fieldInfo.getMethod()));

            if (!fieldInfo.getMethod().getReturnType().equals(Void.TYPE)) {
                mw.visitInsn(Opcodes.POP);
            }
        } else {
            mw.visitFieldInsn(Opcodes.PUTFIELD, ASMUtils.getType(fieldInfo.getDeclaringClass()), fieldInfo.getField().getName(),
                              ASMUtils.getDesc(fieldInfo.getFieldClass()));
        }
    }

    private void _setContext(Context context, MethodVisitor mw) {
        mw.visitVarInsn(Opcodes.ALOAD, 1); // parser
        mw.visitVarInsn(Opcodes.ALOAD, context.var("context"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "setContext",
                           "(Lcom/alibaba/fastjson/parser/ParseContext;)V");

        Label endIf_ = new Label();
        mw.visitVarInsn(Opcodes.ALOAD, context.var("childContext"));
        mw.visitJumpInsn(Opcodes.IFNULL, endIf_);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("childContext"));
        mw.visitVarInsn(Opcodes.ALOAD, context.var("instance"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/ParseContext", "setObject", "(Ljava/lang/Object;)V");

        mw.visitLabel(endIf_);
    }

    private void _deserialize_endCheck(Context context, MethodVisitor mw, Label reset_) {
        Label _end_if = new Label();
        // Label nextToken_ = new Label();

        // mw.visitFieldInsn(GETSTATIC, getType(System.class), "out", "Ljava/io/PrintStream;");
        // mw.visitIntInsn(ILOAD, context.var("matchedCount"));
        // mw.visitMethodInsn(INVOKEVIRTUAL, getType(java.io.PrintStream.class), "println", "(I)V");

        mw.visitIntInsn(Opcodes.ILOAD, context.var("matchedCount"));
        mw.visitJumpInsn(Opcodes.IFLE, reset_);

        // mw.visitFieldInsn(GETSTATIC, getType(System.class), "out", "Ljava/io/PrintStream;");
        // mw.visitVarInsn(ALOAD, context.var("lexer"));
        // mw.visitMethodInsn(INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "token", "()I");
        // mw.visitMethodInsn(INVOKEVIRTUAL, getType(java.io.PrintStream.class), "println", "(I)V");

        mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "token", "()I");
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "RBRACE", "I");
        mw.visitJumpInsn(Opcodes.IF_ICMPNE, reset_);

        // mw.visitLabel(nextToken_);
        mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "COMMA", "I");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "nextToken", "(I)V");

        mw.visitLabel(_end_if);
    }

    private void _deserialze_list_obj(Context context, MethodVisitor mw, Label reset_, FieldInfo fieldInfo,
                                      Class<?> fieldClass, Class<?> itemType, int i) {
        Label matched_ = new Label();
        Label _end_if = new Label();

        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "matchField", "([C)Z");
        mw.visitJumpInsn(Opcodes.IFNE, matched_);
        mw.visitInsn(Opcodes.ACONST_NULL);
        mw.visitVarInsn(Opcodes.ASTORE, context.var(fieldInfo.getName() + "_asm"));

        mw.visitJumpInsn(Opcodes.GOTO, _end_if);

        mw.visitLabel(matched_);
        _setFlag(mw, context, i);

        Label valueNotNull_ = new Label();
        mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "token", "()I");
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "NULL", "I");
        mw.visitJumpInsn(Opcodes.IF_ICMPNE, valueNotNull_);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "COMMA", "I");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "nextToken", "(I)V");

        mw.visitInsn(Opcodes.ACONST_NULL);
        mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(fieldClass)); // cast
        mw.visitVarInsn(Opcodes.ASTORE, context.var(fieldInfo.getName() + "_asm"));
        // loop_end_

        mw.visitLabel(valueNotNull_);
        // if (lexer.token() != JSONToken.LBRACKET) reset
        mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "token", "()I");
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "LBRACKET", "I");

        mw.visitJumpInsn(Opcodes.IF_ICMPNE, reset_);

        _getCollectionFieldItemDeser(context, mw, fieldInfo, itemType);
        mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, "com/alibaba/fastjson/parser/deserializer/ObjectDeserializer", "getFastMatchToken", "()I");
        mw.visitVarInsn(Opcodes.ISTORE, context.var("fastMatchToken"));

        mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("fastMatchToken"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "nextToken", "(I)V");

        _newCollection(mw, fieldClass);

        mw.visitVarInsn(Opcodes.ASTORE, context.var(fieldInfo.getName() + "_asm"));

        { // setContext
            mw.visitVarInsn(Opcodes.ALOAD, 1);
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getContext",
                               "()Lcom/alibaba/fastjson/parser/ParseContext;");
            mw.visitVarInsn(Opcodes.ASTORE, context.var("listContext"));

            mw.visitVarInsn(Opcodes.ALOAD, 1); // parser
            mw.visitVarInsn(Opcodes.ALOAD, context.var(fieldInfo.getName() + "_asm"));
            mw.visitLdcInsn(fieldInfo.getName());
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "setContext",
                               "(Ljava/lang/Object;Ljava/lang/Object;)Lcom/alibaba/fastjson/parser/ParseContext;");
            mw.visitInsn(Opcodes.POP);
        }

        Label loop_ = new Label();
        Label loop_end_ = new Label();

        // for (;;) {
        mw.visitInsn(Opcodes.ICONST_0);
        mw.visitVarInsn(Opcodes.ISTORE, context.var("i"));
        mw.visitLabel(loop_);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "token", "()I");
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "RBRACKET", "I");
        mw.visitJumpInsn(Opcodes.IF_ICMPEQ, loop_end_);

        // Object value = itemDeserializer.deserialze(parser, null);
        // array.add(value);

        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_list_item_deser__",
                          "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
        mw.visitVarInsn(Opcodes.ALOAD, 1);
        mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(itemType)));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("i"));
        mw.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
        mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, "com/alibaba/fastjson/parser/deserializer/ObjectDeserializer", "deserialze",
                           "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;");
        mw.visitVarInsn(Opcodes.ASTORE, context.var("list_item_value"));

        mw.visitIincInsn(context.var("i"), 1);

        mw.visitVarInsn(Opcodes.ALOAD, context.var(fieldInfo.getName() + "_asm"));
        mw.visitVarInsn(Opcodes.ALOAD, context.var("list_item_value"));
        if (fieldClass.isInterface()) {
            mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, ASMUtils.getType(fieldClass), "add", "(Ljava/lang/Object;)Z");
        } else {
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, ASMUtils.getType(fieldClass), "add", "(Ljava/lang/Object;)Z");
        }
        mw.visitInsn(Opcodes.POP);

        mw.visitVarInsn(Opcodes.ALOAD, 1);
        mw.visitVarInsn(Opcodes.ALOAD, context.var(fieldInfo.getName() + "_asm"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "checkListResolve",
                           "(Ljava/util/Collection;)V");

        // if (lexer.token() == JSONToken.COMMA) {
        // lexer.nextToken(itemDeserializer.getFastMatchToken());
        // continue;
        // }
        mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "token", "()I");
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "COMMA", "I");
        mw.visitJumpInsn(Opcodes.IF_ICMPNE, loop_);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
        mw.visitVarInsn(Opcodes.ILOAD, context.var("fastMatchToken"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "nextToken", "(I)V");
        mw.visitJumpInsn(Opcodes.GOTO, loop_);

        mw.visitLabel(loop_end_);

        // mw.visitVarInsn(ASTORE, context.var("context"));
        // parser.setContext(context);
        { // setContext
            mw.visitVarInsn(Opcodes.ALOAD, 1); // parser
            mw.visitVarInsn(Opcodes.ALOAD, context.var("listContext"));
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "setContext",
                               "(Lcom/alibaba/fastjson/parser/ParseContext;)V");
        }

        mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "token", "()I");
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "RBRACKET", "I");
        mw.visitJumpInsn(Opcodes.IF_ICMPNE, reset_);

        mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/JSONToken", "COMMA", "I");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "nextToken", "(I)V");
        // lexer.nextToken(JSONToken.COMMA);

        mw.visitLabel(_end_if);
    }

    private void _getCollectionFieldItemDeser(Context context, MethodVisitor mw, FieldInfo fieldInfo, Class<?> itemType) {
        Label notNull_ = new Label();
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_list_item_deser__",
                          "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
        mw.visitJumpInsn(Opcodes.IFNONNULL, notNull_);

        mw.visitVarInsn(Opcodes.ALOAD, 0);

        mw.visitVarInsn(Opcodes.ALOAD, 1);
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getConfig", "()"
                                                                                         + "Lcom/alibaba/fastjson/parser/ParserConfig;");
        mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(itemType)));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/ParserConfig", "getDeserializer",
                           "(Ljava/lang/reflect/Type;)Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");

        mw.visitFieldInsn(Opcodes.PUTFIELD, context.getClassName(), fieldInfo.getName() + "_asm_list_item_deser__",
                          "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");

        mw.visitLabel(notNull_);
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_list_item_deser__",
                          "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
    }

    private void _newCollection(MethodVisitor mw, Class<?> fieldClass) {
        if (fieldClass.isAssignableFrom(ArrayList.class)) {
            mw.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList");
            mw.visitInsn(Opcodes.DUP);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V");
        } else if (fieldClass.isAssignableFrom(LinkedList.class)) {
            mw.visitTypeInsn(Opcodes.NEW, ASMUtils.getType(LinkedList.class));
            mw.visitInsn(Opcodes.DUP);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, ASMUtils.getType(LinkedList.class), "<init>", "()V");
        } else if (fieldClass.isAssignableFrom(HashSet.class)) {
            mw.visitTypeInsn(Opcodes.NEW, ASMUtils.getType(HashSet.class));
            mw.visitInsn(Opcodes.DUP);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, ASMUtils.getType(HashSet.class), "<init>", "()V");
        } else if (fieldClass.isAssignableFrom(TreeSet.class)) {
            mw.visitTypeInsn(Opcodes.NEW, ASMUtils.getType(TreeSet.class));
            mw.visitInsn(Opcodes.DUP);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, ASMUtils.getType(TreeSet.class), "<init>", "()V");
        } else {
            mw.visitTypeInsn(Opcodes.NEW, ASMUtils.getType(fieldClass));
            mw.visitInsn(Opcodes.DUP);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, ASMUtils.getType(fieldClass), "<init>", "()V");
        }
        mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(fieldClass)); // cast
    }

    private void _deserialze_obj(Context context, MethodVisitor mw, Label reset_, FieldInfo fieldInfo,
                                 Class<?> fieldClass, int i) {
        Label matched_ = new Label();
        Label _end_if = new Label();

        mw.visitVarInsn(Opcodes.ALOAD, context.var("lexer"));
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/JSONLexerBase", "matchField", "([C)Z");
        mw.visitJumpInsn(Opcodes.IFNE, matched_);
        mw.visitInsn(Opcodes.ACONST_NULL);
        mw.visitVarInsn(Opcodes.ASTORE, context.var(fieldInfo.getName() + "_asm"));

        mw.visitJumpInsn(Opcodes.GOTO, _end_if);

        mw.visitLabel(matched_);

        _setFlag(mw, context, i);

        // increment matchedCount
        mw.visitVarInsn(Opcodes.ILOAD, context.var("matchedCount"));
        mw.visitInsn(Opcodes.ICONST_1);
        mw.visitInsn(Opcodes.IADD);
        mw.visitVarInsn(Opcodes.ISTORE, context.var("matchedCount"));

        _deserObject(context, mw, fieldInfo, fieldClass);

        mw.visitVarInsn(Opcodes.ALOAD, 1);
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getResolveStatus", "()I");
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/DefaultJSONParser", "NeedToResolve", "I");
        mw.visitJumpInsn(Opcodes.IF_ICMPNE, _end_if);

        // ResolveTask task = parser.getLastResolveTask();
        // task.setFieldDeserializer(this);
        // task.setOwnerContext(parser.getContext());

        mw.visitVarInsn(Opcodes.ALOAD, 1);
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getLastResolveTask",
                           "()Lcom/alibaba/fastjson/parser/DefaultJSONParser$ResolveTask;");
        mw.visitVarInsn(Opcodes.ASTORE, context.var("resolveTask"));

        mw.visitVarInsn(Opcodes.ALOAD, context.var("resolveTask"));
        mw.visitVarInsn(Opcodes.ALOAD, 1);
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getContext", "()"
                                                                                          + "Lcom/alibaba/fastjson/parser/ParseContext;");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser$ResolveTask"
                           , "setOwnerContext"
                           , "(Lcom/alibaba/fastjson/parser/ParseContext;)V");

        mw.visitVarInsn(Opcodes.ALOAD, context.var("resolveTask"));
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitLdcInsn(fieldInfo.getName());
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", "getFieldDeserializer",
                           "(Ljava/lang/String;)Lcom/alibaba/fastjson/parser/deserializer/FieldDeserializer;");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser$ResolveTask", "setFieldDeserializer",
                           "(Lcom/alibaba/fastjson/parser/deserializer/FieldDeserializer;)V");

        mw.visitVarInsn(Opcodes.ALOAD, 1);
        mw.visitFieldInsn(Opcodes.GETSTATIC, "com/alibaba/fastjson/parser/DefaultJSONParser", "NONE", "I");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "setResolveStatus", "(I)V");

        mw.visitLabel(_end_if);

    }

    private void _deserObject(Context context, MethodVisitor mw, FieldInfo fieldInfo, Class<?> fieldClass) {
        _getFieldDeser(context, mw, fieldInfo);

        mw.visitVarInsn(Opcodes.ALOAD, 1);
        if (fieldInfo.getFieldType() instanceof Class) {
            mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(fieldInfo.getFieldClass())));
        } else {
            mw.visitVarInsn(Opcodes.ALOAD, 0);
            mw.visitLdcInsn(fieldInfo.getName());
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", "getFieldType",
                               "(Ljava/lang/String;)Ljava/lang/reflect/Type;");
        }
        mw.visitLdcInsn(fieldInfo.getName());
        mw.visitMethodInsn(Opcodes.INVOKEINTERFACE, "com/alibaba/fastjson/parser/deserializer/ObjectDeserializer", "deserialze",
                           "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;");
        mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(fieldClass)); // cast
        mw.visitVarInsn(Opcodes.ASTORE, context.var(fieldInfo.getName() + "_asm"));
    }

    private void _getFieldDeser(Context context, MethodVisitor mw, FieldInfo fieldInfo) {
        Label notNull_ = new Label();
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_deser__",
                          "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
        mw.visitJumpInsn(Opcodes.IFNONNULL, notNull_);

        mw.visitVarInsn(Opcodes.ALOAD, 0);

        mw.visitVarInsn(Opcodes.ALOAD, 1);
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/DefaultJSONParser", "getConfig", "()"
                                                                                         + "Lcom/alibaba/fastjson/parser/ParserConfig;");
        mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(ASMUtils.getDesc(fieldInfo.getFieldClass())));
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/ParserConfig", "getDeserializer",
                           "(Ljava/lang/reflect/Type;)Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");

        mw.visitFieldInsn(Opcodes.PUTFIELD, context.getClassName(), fieldInfo.getName() + "_asm_deser__",
                          "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");

        mw.visitLabel(notNull_);

        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitFieldInsn(Opcodes.GETFIELD, context.getClassName(), fieldInfo.getName() + "_asm_deser__",
                          "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
    }

    public FieldDeserializer createFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo)
                                                                                                               throws Exception {
        Class<?> fieldClass = fieldInfo.getFieldClass();

        if (fieldClass == int.class || fieldClass == long.class || fieldClass == String.class) {
            return createStringFieldDeserializer(mapping, clazz, fieldInfo);
        }

        FieldDeserializer fieldDeserializer = mapping.createFieldDeserializerWithoutASM(mapping, clazz, fieldInfo);
        return fieldDeserializer;
    }

    public FieldDeserializer createStringFieldDeserializer(ParserConfig mapping, Class<?> clazz, FieldInfo fieldInfo)
                                                                                                                     throws Exception {
        Class<?> fieldClass = fieldInfo.getFieldClass();
        Method method = fieldInfo.getMethod();

        String className = getGenFieldDeserializer(clazz, fieldInfo);

        ClassWriter cw = new ClassWriter();
        Class<?> superClass;
        if (fieldClass == int.class) {
            superClass = IntegerFieldDeserializer.class;
        } else if (fieldClass == long.class) {
            superClass = LongFieldDeserializer.class;
        } else {
            superClass = StringFieldDeserializer.class;
        }

        int INVAKE_TYPE;
        if (clazz.isInterface()) {
            INVAKE_TYPE = Opcodes.INVOKEINTERFACE;
        } else {
            INVAKE_TYPE = Opcodes.INVOKEVIRTUAL;
        }

        cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, className, ASMUtils.getType(superClass), null);

        {
            MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC //
                                              , "<init>" //
                                              , "(Lcom/alibaba/fastjson/parser/ParserConfig;Ljava/lang/Class;Lcom/alibaba/fastjson/util/FieldInfo;)V", null, null);
            mw.visitVarInsn(Opcodes.ALOAD, 0);
            mw.visitVarInsn(Opcodes.ALOAD, 1);
            mw.visitVarInsn(Opcodes.ALOAD, 2);
            mw.visitVarInsn(Opcodes.ALOAD, 3);
            mw.visitMethodInsn(Opcodes.INVOKESPECIAL, ASMUtils.getType(superClass), "<init>"
                               , "(Lcom/alibaba/fastjson/parser/ParserConfig;Ljava/lang/Class;Lcom/alibaba/fastjson/util/FieldInfo;)V");

            mw.visitInsn(Opcodes.RETURN);
            mw.visitMaxs(4, 6);
            mw.visitEnd();
        }

        if (method != null) {
            if (fieldClass == int.class) {
                MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "setValue", "(Ljava/lang/Object;I)V", null,
                                                  null);
                mw.visitVarInsn(Opcodes.ALOAD, 1);
                mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(method.getDeclaringClass())); // cast
                mw.visitVarInsn(Opcodes.ILOAD, 2);
                mw.visitMethodInsn(INVAKE_TYPE, ASMUtils.getType(method.getDeclaringClass()), method.getName(),
                                   ASMUtils.getDesc(method));

                mw.visitInsn(Opcodes.RETURN);
                mw.visitMaxs(3, 3);
                mw.visitEnd();
            } else if (fieldClass == long.class) {
                MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "setValue", "(Ljava/lang/Object;J)V", null,
                                                  null);
                mw.visitVarInsn(Opcodes.ALOAD, 1);
                mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(method.getDeclaringClass())); // cast
                mw.visitVarInsn(Opcodes.LLOAD, 2);
                mw.visitMethodInsn(INVAKE_TYPE, ASMUtils.getType(method.getDeclaringClass()), method.getName(),
                                   ASMUtils.getDesc(method));

                mw.visitInsn(Opcodes.RETURN);
                mw.visitMaxs(3, 4);
                mw.visitEnd();
            } else {
                // public void setValue(Object object, Object value)
                MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "setValue", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
                mw.visitVarInsn(Opcodes.ALOAD, 1);
                mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(method.getDeclaringClass())); // cast
                mw.visitVarInsn(Opcodes.ALOAD, 2);
                mw.visitTypeInsn(Opcodes.CHECKCAST, ASMUtils.getType(fieldClass)); // cast
                mw.visitMethodInsn(INVAKE_TYPE, ASMUtils.getType(method.getDeclaringClass()), method.getName(),
                                   ASMUtils.getDesc(method));

                mw.visitInsn(Opcodes.RETURN);
                mw.visitMaxs(3, 3);
                mw.visitEnd();
            }
        }

        byte[] code = cw.toByteArray();

        Class<?> exampleClass = classLoader.defineClassPublic(className, code, 0, code.length);

        Constructor<?> constructor = exampleClass.getConstructor(ParserConfig.class, Class.class, FieldInfo.class);
        Object instance = constructor.newInstance(mapping, clazz, fieldInfo);

        return (FieldDeserializer) instance;
    }

    static class Context {

        private int                       variantIndex = 5;

        private Map<String, Integer>      variants     = new HashMap<String, Integer>();

        private Class<?>                  clazz;
        private final DeserializeBeanInfo beanInfo;
        private String                    className;
        private List<FieldInfo>           fieldInfoList;

        public Context(String className, ParserConfig config, DeserializeBeanInfo beanInfo, int initVariantIndex){
            this.className = className;
            this.clazz = beanInfo.getClazz();
            this.variantIndex = initVariantIndex;
            this.beanInfo = beanInfo;
            fieldInfoList = new ArrayList<FieldInfo>(beanInfo.getFieldList());
        }

        public String getClassName() {
            return className;
        }

        public List<FieldInfo> getFieldInfoList() {
            return fieldInfoList;
        }

        public DeserializeBeanInfo getBeanInfo() {
            return beanInfo;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public int getVariantCount() {
            return variantIndex;
        }

        public int var(String name, int increment) {
            Integer i = variants.get(name);
            if (i == null) {
                variants.put(name, variantIndex);
                variantIndex += increment;
            }
            i = variants.get(name);
            return i.intValue();
        }

        public int var(String name) {
            Integer i = variants.get(name);
            if (i == null) {
                variants.put(name, variantIndex++);
            }
            i = variants.get(name);
            return i.intValue();
        }
    }

    private void _init(ClassWriter cw, Context context) {
        for (int i = 0, size = context.getFieldInfoList().size(); i < size; ++i) {
            FieldInfo fieldInfo = context.getFieldInfoList().get(i);

            // public FieldVisitor visitField(final int access, final String name, final String desc, final String
            // signature, final Object value) {
            FieldVisitor fw = cw.visitField(Opcodes.ACC_PUBLIC, fieldInfo.getName() + "_asm_prefix__", "[C");
            fw.visitEnd();
        }

        for (int i = 0, size = context.getFieldInfoList().size(); i < size; ++i) {
            FieldInfo fieldInfo = context.getFieldInfoList().get(i);
            Class<?> fieldClass = fieldInfo.getFieldClass();

            if (fieldClass.isPrimitive()) {
                continue;
            }

            if (fieldClass.isEnum()) {
                continue;
            }

            if (Collection.class.isAssignableFrom(fieldClass)) {
                FieldVisitor fw = cw.visitField(Opcodes.ACC_PUBLIC, fieldInfo.getName() + "_asm_list_item_deser__",
                                                "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
                fw.visitEnd();
            } else {
                FieldVisitor fw = cw.visitField(Opcodes.ACC_PUBLIC, fieldInfo.getName() + "_asm_deser__",
                                                "Lcom/alibaba/fastjson/parser/deserializer/ObjectDeserializer;");
                fw.visitEnd();
            }
        }

        MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(Lcom/alibaba/fastjson/parser/ParserConfig;Ljava/lang/Class;)V", null, null);
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitVarInsn(Opcodes.ALOAD, 1);
        mw.visitVarInsn(Opcodes.ALOAD, 2);
        mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", "<init>",
                           "(Lcom/alibaba/fastjson/parser/ParserConfig;Ljava/lang/Class;)V");

        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitFieldInsn(Opcodes.GETFIELD, "com/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer", "serializer",
                          "Lcom/alibaba/fastjson/parser/deserializer/ASMJavaBeanDeserializer$InnerJavaBeanDeserializer;");
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "com/alibaba/fastjson/parser/deserializer/JavaBeanDeserializer", "getFieldDeserializerMap",
                           "()Ljava/util/Map;");
        mw.visitInsn(Opcodes.POP);

        // init fieldNamePrefix
        for (int i = 0, size = context.getFieldInfoList().size(); i < size; ++i) {
            FieldInfo fieldInfo = context.getFieldInfoList().get(i);

            mw.visitVarInsn(Opcodes.ALOAD, 0);
            mw.visitLdcInsn("\"" + fieldInfo.getName() + "\":"); // public char[] toCharArray()
            mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C");
            mw.visitFieldInsn(Opcodes.PUTFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");

        }

        mw.visitInsn(Opcodes.RETURN);
        mw.visitMaxs(4, 4);
        mw.visitEnd();
    }

    private void _createInstance(ClassWriter cw, Context context) {
        MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "createInstance"
                                          , "(Lcom/alibaba/fastjson/parser/DefaultJSONParser;Ljava/lang/reflect/Type;)Ljava/lang/Object;",
                                          null, null);

        mw.visitTypeInsn(Opcodes.NEW, ASMUtils.getType(context.getClazz()));
        mw.visitInsn(Opcodes.DUP);
        mw.visitMethodInsn(Opcodes.INVOKESPECIAL, ASMUtils.getType(context.getClazz()), "<init>", "()V");

        mw.visitInsn(Opcodes.ARETURN);
        mw.visitMaxs(3, 3);
        mw.visitEnd();
    }

}

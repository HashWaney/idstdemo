package com.android.processor;

import com.android.annotation.Annotation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Created by HashWaney on 2019/8/23.
 */

@AutoService(Processor.class)
public class Processors extends AbstractProcessor {

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        //Filer 是一个接口,支持通过注解器创建新的文件
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement element : annotations) {
            //新建文件
            if (element.getQualifiedName().toString().equalsIgnoreCase(Annotation.class.getCanonicalName())) {
                //创建main方法
                MethodSpec main = MethodSpec.methodBuilder("main")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(void.class)
                        .addParameter(String[].class, "args")
                        .addStatement("$T.out.println($S)", System.class, "Hello,JavaPoet!")
                        .build();
                //创建HelloWorld类
                TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addMethod(main)
                        .build();
                try {
                    //生成com.example.HelloWorld.java
                    JavaFile javaFile = JavaFile.builder("com.example", helloWorld)
                            .addFileComment("This codes are generated automatically,Do not modify ")
                            .build();
                    //生成文件
                    javaFile.writeTo(filer);

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }
        //在Gradle console 打印日志
        for (Element element : roundEnv.getElementsAnnotatedWith(Annotation.class)) {
            System.out.println("--------------------------------------");
            //判断元素的类型为Class
            if (element.getKind() == ElementKind.CLASS) {
                //显示转换元素类型
                TypeElement typeElement = (TypeElement) element;
                System.err.println("typeElement name:" + typeElement.getSimpleName());
            }
            System.out.println("--------------------------------------");
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> annotations = new LinkedHashSet<>();
        annotations.add(Annotation.class.getCanonicalName());
        return annotations;
    }
}

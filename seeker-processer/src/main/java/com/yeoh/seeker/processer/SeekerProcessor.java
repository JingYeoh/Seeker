package com.yeoh.seeker.processer;

import com.google.auto.service.AutoService;
import com.yeoh.seeker.annotation.Hide;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * The processor used to process {@link com.yeoh.seeker.annotation.Hide} annotation .
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-08-21
 */
@SupportedAnnotationTypes("com.yeoh.seeker.annotation.Hide")
@SupportedOptions({SeekerProcessor.OPTION_MODULE_NAME, SeekerProcessor.OPTION_SUB_MODULES})
@AutoService(Processor.class)
public class SeekerProcessor extends AbstractProcessor {

    static final String OPTION_MODULE_NAME = "moduleNameOfSeeker";
    static final String OPTION_SUB_MODULES = "subModulesOfSeeker";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        Log.d("========================= SEEKER PROCESSOR ===================================");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element it: roundEnvironment.getElementsAnnotatedWith(Hide.class)) {
            if (it instanceof ExecutableElement) {
                processMethodElement((ExecutableElement) it);
            }
        }
        String moduleName = processingEnv.getOptions().get(OPTION_MODULE_NAME);
        String subModules = processingEnv.getOptions().get(OPTION_SUB_MODULES);
        Log.d("## subModules: " + subModules);
        Log.d("## moduleName: " + moduleName);
        return true;
    }

    private void processMethodElement(ExecutableElement element) {
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();

        String className = typeElement.getQualifiedName().toString();
        String methodName = element.getSimpleName().toString();
        List<String> params = new ArrayList<>();

        Log.d("## found method " + className + "." + methodName);

        for (VariableElement it: element.getParameters()) {
            TypeMirror methodParameterType = it.asType();
            String paramClassName = methodParameterType.toString();
            params.add(paramClassName);
            Log.d("#### method params: " + paramClassName);
        }

    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }
}

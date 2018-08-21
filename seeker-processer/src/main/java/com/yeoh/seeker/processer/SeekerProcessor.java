package com.yeoh.seeker.processer;

import com.google.auto.service.AutoService;
import com.yeoh.seeker.annotation.Hide;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
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
@AutoService(Processor.class)
public class SeekerProcessor extends AbstractProcessor {

    private Map<String, List<HideMethod>> mAnnotatedHideMap;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mAnnotatedHideMap = new HashMap<>();
        Log.d("========================= SEEKER PROCESSOR ===================================");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element it : roundEnvironment.getElementsAnnotatedWith(Hide.class)) {
            if (it instanceof ExecutableElement) {
                processMethodElement((ExecutableElement) it);
            }
        }
        return new SeekerDelegateGenerator(mAnnotatedHideMap).generate();
    }

    private void processMethodElement(ExecutableElement element) {
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();

        String className = typeElement.getQualifiedName().toString();
        String methodName = element.getSimpleName().toString();

        Log.d("## found method " + className + "." + methodName);
        List<String> params = new ArrayList<>();

        for (VariableElement it : element.getParameters()) {
            TypeMirror methodParameterType = it.asType();
            String paramClassName = methodParameterType.toString();
            params.add(paramClassName);
            Log.d("#### method params: " + paramClassName);
        }
        List<HideMethod> seekerMethods = mAnnotatedHideMap.get(className);
        if (seekerMethods == null) {
            seekerMethods = new ArrayList<>();
        }
        seekerMethods.add(new HideMethod(methodName, params));

        mAnnotatedHideMap.put(className, seekerMethods);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Hide.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }
}

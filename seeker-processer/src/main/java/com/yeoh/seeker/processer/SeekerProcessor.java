package com.yeoh.seeker.processer;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

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

    private Filer mFiler;
    private boolean mHasProcess;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        Log.print("========================= SEEKER PROCESSOR ===================================");
        mFiler = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        String moduleName = processingEnv.getOptions().get(OPTION_MODULE_NAME);
        String subModules = processingEnv.getOptions().get(OPTION_SUB_MODULES);
        if (mHasProcess) {
            return false;
        }
        try {
            mHasProcess = new SeekerDelegateGenerator(subModules, moduleName, mFiler, roundEnvironment).generate();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mHasProcess;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }
}

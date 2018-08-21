package com.yeoh.seeker.processer.model;

import java.util.ArrayList;
import java.util.List;
import javax.lang.model.type.TypeMirror;

/**
 * Used to save method info
 *
 * @author yangjing @ Zhihu Inc.
 * @since 2018-08-21
 */
public class MethodInfo {

    private String name;
    private ClassInfo returnType;
    private List<ClassInfo> thrownTypes;
    private List<ClassInfo> parameterTypes;
    private String methodContentCode;

    public MethodInfo(String name, TypeMirror returnType, List<ClassInfo> parameterTypes,
            List<? extends TypeMirror> thrownTypes) {
        this.name = name;
        this.returnType = new ClassInfo(returnType);
        this.parameterTypes = parameterTypes;
        this.methodContentCode = generateMethodContentCode();
        this.thrownTypes = new ArrayList<>();
        for (TypeMirror throwType : thrownTypes) {
            this.thrownTypes.add(new ClassInfo(throwType));
        }
    }

    public List<ClassInfo> getThrownTypes() {
        return thrownTypes;
    }

    public String getName() {
        return name;
    }

    public ClassInfo getReturnType() {
        return returnType;
    }

    public List<ClassInfo> getParameterTypes() {
        return parameterTypes;
    }

    public boolean isVoid() {
        return returnType.className.equals("void");
    }

    private String generateMethodContentCode() {
        StringBuilder builder = new StringBuilder();
        if (this.returnType.fullName.equals("void")) {
            builder.append("mData.").append(name).append("(");
        } else {
            builder.append("return mData.").append(name).append("(");
        }
        if (parameterTypes != null && parameterTypes.size() > 0) {
            for (int i = 1; i <= parameterTypes.size(); i++) {
                builder.append("$").append(i);
                if (i != parameterTypes.size()) {
                    builder.append(",");
                }
            }
        }
        builder.append(");");
        return builder.toString();
    }
}

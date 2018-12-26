package com.factory;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Method;
import java.util.List;

public class Utils {

    public static MethodNode getMethodNode(List<MethodNode> methodNodes, Method method) {
        String typeDesc = Type.getMethodDescriptor(method);
        for (MethodNode methodNode : methodNodes) {
            if (StringUtils.equals(methodNode.desc, typeDesc) && StringUtils.equals(methodNode.name, method.getName()))
                return methodNode;
        }
        return null;
    }
}

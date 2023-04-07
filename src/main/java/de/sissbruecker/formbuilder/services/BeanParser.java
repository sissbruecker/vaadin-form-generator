package de.sissbruecker.formbuilder.services;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import de.sissbruecker.formbuilder.model.BeanModel;
import de.sissbruecker.formbuilder.model.BeanProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BeanParser {
    public Optional<BeanModel> parse(String source) {
        CompilationUnit cu = StaticJavaParser.parse(source);

        Optional<ClassOrInterfaceDeclaration> maybeClassDeclaration = cu.findFirst(ClassOrInterfaceDeclaration.class);
        if (maybeClassDeclaration.isEmpty()) {
            return Optional.empty();
        }

        List<BeanProperty> properties = new ArrayList<>();

        for (MethodDeclaration method : maybeClassDeclaration.get().findAll(MethodDeclaration.class)) {
            if (!method.getModifiers().contains(Modifier.publicModifier())) {
                continue;
            }
            if (!(method.getNameAsString().startsWith("get") || method.getNameAsString().startsWith("is"))) {
                continue;
            }
            if (method.getParameters().size() > 0) {
                continue;
            }

            String fieldName;
            if (method.getNameAsString().startsWith("get")) {
                fieldName = method.getNameAsString().substring(3);
            } else {
                fieldName = method.getNameAsString().substring(2);
            }

            Optional<MethodDeclaration> maybeSetter = method.getParentNode()
                    .flatMap(node -> node.findFirst(MethodDeclaration.class,
                            md -> md.getModifiers().contains(Modifier.publicModifier()) &&
                                  md.getNameAsString().equals("set" + fieldName) &&
                                  md.getParameters().size() == 1 &&
                                  md.getParameter(0).getType().equals(method.getType()))
                    );

            if (maybeSetter.isPresent()) {
                String lowerCaseFieldName = fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                properties.add(new BeanProperty(lowerCaseFieldName, method.getType().asString()));
            }
        }

        return Optional.of(new BeanModel(maybeClassDeclaration.get().getNameAsString(), properties));
    }
}
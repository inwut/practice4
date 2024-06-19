package org.ukma;

import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("org.ukma.GenerateBuilder")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class GenerateBuilderProcessor extends AbstractProcessor {
    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(GenerateBuilder.class)) {
            if (element.getKind().isClass()) {
                try {
                    generate((TypeElement) element);
                } catch (IOException e) {
                    messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                }
            }
        }
        return true;
    }

    private void generate(TypeElement typeElement) throws IOException {
        String className = typeElement.getSimpleName() + "Builder";
        String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
        List<VariableElement> fields = ElementFilter.fieldsIn(typeElement.getEnclosedElements());

        MethodSpec.Builder buildMethodBuilder = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(packageName, typeElement.getSimpleName().toString()));

        StringBuilder buildStatement = new StringBuilder("return new $T(");
        for (VariableElement field : fields) {
            String fieldName = field.getSimpleName().toString();
            buildStatement.append(fieldName).append(", ");
        }
        if (!fields.isEmpty()) {
            buildStatement.setLength(buildStatement.length() - 2);
        }
        buildStatement.append(")");

        buildMethodBuilder.addStatement(buildStatement.toString(), ClassName.get(packageName, typeElement.getSimpleName().toString()));
        MethodSpec buildMethod = buildMethodBuilder.build();

        TypeSpec.Builder builderClassBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(buildMethod);

        for (VariableElement field : fields) {
            String fieldName = field.getSimpleName().toString();
            TypeName fieldType = TypeName.get(field.asType());
            builderClassBuilder.addField(fieldType, fieldName, Modifier.PRIVATE);

            MethodSpec setterMethod = MethodSpec.methodBuilder(fieldName)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ClassName.get(packageName, className))
                    .addParameter(fieldType, fieldName)
                    .addStatement("this.$L = $L", fieldName, fieldName)
                    .addStatement("return this")
                    .build();

            builderClassBuilder.addMethod(setterMethod);
        }

        TypeSpec builderClass = builderClassBuilder.build();
        JavaFile javaFile = JavaFile.builder(packageName, builderClass)
                .build();
        javaFile.writeTo(filer);
    }
}

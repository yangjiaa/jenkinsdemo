package com.yj.jenkinsdemo.util;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Example类和model类实现序列化插件
 */
public class SerializablePlugin extends PluginAdapter {
    private FullyQualifiedJavaType serializable = new FullyQualifiedJavaType("java.io.Serializable");
    private FullyQualifiedJavaType persistence = new FullyQualifiedJavaType("javax.persistence.*");
    private FullyQualifiedJavaType genericGenerator = new FullyQualifiedJavaType("org.hibernate.annotations.GenericGenerator");
    private FullyQualifiedJavaType gwtSerializable = new FullyQualifiedJavaType("com.google.gwt.user.client.rpc.IsSerializable");
    private FullyQualifiedJavaType repository = new FullyQualifiedJavaType("org.springframework.stereotype.Repository");
    private boolean addGWTInterface;
    private boolean suppressJavaInterface;

    public SerializablePlugin() {
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        this.addGWTInterface = Boolean.valueOf(properties.getProperty("addGWTInterface")).booleanValue();
        this.suppressJavaInterface = Boolean.valueOf(properties.getProperty("suppressJavaInterface")).booleanValue();
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        super.clientGenerated(interfaze,topLevelClass,introspectedTable);
        interfaze.addImportedType(repository);
        interfaze.addAnnotation("@Repository");
        return true;
    }
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.makeJpa(topLevelClass,introspectedTable);
        this.makeSerializable(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.makeSerializable(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        this.makeSerializable(topLevelClass, introspectedTable);
        return true;
    }

    protected void makeSerializable(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if(this.addGWTInterface) {
            topLevelClass.addImportedType(this.gwtSerializable);
            topLevelClass.addSuperInterface(this.gwtSerializable);
        }
        if(!this.suppressJavaInterface) {
            topLevelClass.addImportedType(this.serializable);
//            topLevelClass.addImportedType(this.persistence);
            topLevelClass.addSuperInterface(this.serializable);
            Field field = new Field();
            field.setFinal(true);
            field.setInitializationString("1L");
            field.setName("serialVersionUID");
            field.setStatic(true);
            field.setType(new FullyQualifiedJavaType("long"));
            field.setVisibility(JavaVisibility.PRIVATE);
//
//            if(!topLevelClass.getType().toString().endsWith("Example")){
//                topLevelClass.addAnnotation("@Entity");
//                topLevelClass.addAnnotation("@Table(name = \""+introspectedTable.getFullyQualifiedTable()+"\")");
//                for(Field f:topLevelClass.getFields()){
//                    f.addAnnotation("@Column");
//                }
//            }
            this.context.getCommentGenerator().addFieldComment(field, introspectedTable);
            topLevelClass.addField(field);
        }

    }
    protected void makeJpa(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType(this.persistence);
        if(!topLevelClass.getType().toString().endsWith("Example")){
            topLevelClass.addAnnotation("@Entity");
            topLevelClass.addAnnotation("@Table(name = \""+introspectedTable.getFullyQualifiedTable()+"\")");
            List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
            List<String> primaryKeys = new ArrayList<>();
            for(IntrospectedColumn column:primaryKeyColumns){
                primaryKeys.add(column.getJavaProperty());
            }
            boolean useGenericGenerator = false;
            for(Field f:topLevelClass.getFields()){
                if(primaryKeys.contains(f.getName())){
                    f.addAnnotation("@Id");
                    if("java.lang.String".equals(f.getType().toString())){
                        useGenericGenerator =true;
                        f.addAnnotation("@GeneratedValue(generator=\"system-uuid\")");
                        f.addAnnotation("@GenericGenerator(name = \"system-uuid\",strategy=\"uuid\")");
                    }
                }else{
                    f.addAnnotation("@Column");
                }
            }
            if(useGenericGenerator){
                topLevelClass.addImportedType(this.genericGenerator);
            }
        }
    }

    /**
     * 添加给Example类序列化的方法
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,IntrospectedTable introspectedTable){
        makeSerializable(topLevelClass, introspectedTable);

        for (InnerClass innerClass : topLevelClass.getInnerClasses()) {
            if ("GeneratedCriteria".equals(innerClass.getType().getShortName())) {
                innerClass.addSuperInterface(serializable);
            }
            if ("Criteria".equals(innerClass.getType().getShortName())) {
                innerClass.addSuperInterface(serializable);
            }
            if ("Criterion".equals(innerClass.getType().getShortName())) {
                innerClass.addSuperInterface(serializable);
            }
        }

        return true;
    }

}

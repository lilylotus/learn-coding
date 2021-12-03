package cn.nihility.profile.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 在 spring 容器中这个 bean 的名称为
 *     <prefix>-<fqn> <ConfigurationProperties 的 prefix 值>-<类的全限定名称>
 *     property-cn.nihility.profile.bean.PropertyBean
 */
@ConfigurationProperties(prefix = "property")
public class PropertyBean {

    private String propertyName;
    private int propertyAge;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public int getPropertyAge() {
        return propertyAge;
    }

    public void setPropertyAge(int propertyAge) {
        this.propertyAge = propertyAge;
    }

    @Override
    public String toString() {
        return "PropertyBean{" +
            "id = " + hashCode() +
            "propertyName='" + propertyName + '\'' +
            ", propertyAge=" + propertyAge +
            '}';
    }
}

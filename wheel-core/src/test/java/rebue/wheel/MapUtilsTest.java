package rebue.wheel;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

public class MapUtilsTest {
    public class Student {
        private Long   id;
        private String name;
        private Short  age;
        private Date   birthday;

        public Long getId() {
            return id;
        }

        public void setId(final Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public Short getAge() {
            return age;
        }

        public void setAge(final Short age) {
            this.age = age;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(final Date birthday) {
            this.birthday = birthday;
        }

        @Override
        public String toString() {
            return "Student [id=" + id + ", name=" + name + ", age=" + age + ", birthday=" + birthday + "]";
        }

    }

    @Test
    public void test01() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException {
        final Student zs = new Student();
        zs.setId(1L);
        zs.setName("张三");

        final Map<?, ?> map = MapUtils.bean2Map(zs);
        for (final Entry<?, ?> item : map.entrySet()) {
            System.out.println(item.getKey() + ":" + item.getValue());
        }
    }
}

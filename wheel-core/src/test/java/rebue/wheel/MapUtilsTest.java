package rebue.wheel;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

public class MapUtilsTest {
    public class Student {
        private Long   id;
        private String name;
        private Short  age;
        private Date   birthday;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Short getAge() {
            return age;
        }

        public void setAge(Short age) {
            this.age = age;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }

        @Override
        public String toString() {
            return "Student [id=" + id + ", name=" + name + ", age=" + age + ", birthday=" + birthday + "]";
        }

    }

    @Test
    public void test01() {
        Student zs = new Student();
        zs.setId(1L);
        zs.setName("张三");

        Map<?, ?> map = MapUtils.obj2Map(zs);
        for (Entry<?, ?> item : map.entrySet()) {
            System.out.println(item.getKey() + ":" + item.getValue());
        }
    }
}

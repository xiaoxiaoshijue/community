package life.majiang.community.community.provider;

import org.springframework.stereotype.Component;

@Component
public class Constant {
    public static final String QUESTION_INFO_KEY = "cache:question";
    public static final String TOPPING_QUESTION_ID = "4";


    public static enum sexConstant{
        MAN("1","男"),
        FEMAN("2","女");

        private sexConstant(String value,String name){
            this.value = value;
            this.name = name;
        }
        private final String value;
        private final String name;

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }
}

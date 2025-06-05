package xiaozhi.modules.student.enums;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

public enum StudentGender implements IEnum<Integer> {
    GIRL(0),
    BOY(1);

    @EnumValue
    private final int value;

    StudentGender(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
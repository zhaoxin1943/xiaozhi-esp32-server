package xiaozhi.modules.student.enums;
import com.baomidou.mybatisplus.annotation.EnumValue;

public enum StudentGender {
    @EnumValue
    GIRL(0),
    @EnumValue
    BOY(1);

    private final int value;

    StudentGender(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
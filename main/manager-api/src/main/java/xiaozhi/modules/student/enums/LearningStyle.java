package xiaozhi.modules.student.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum LearningStyle {
    STRUCTURED(0, "结构化学习"),
    FREE_TALK(1, "自动对话"),
    CORRECTIVE(2, "注重纠错");

    @EnumValue
    private final int code;

    private final String description;

    LearningStyle(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

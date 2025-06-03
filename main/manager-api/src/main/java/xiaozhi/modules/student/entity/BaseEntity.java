package xiaozhi.modules.student.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public abstract class BaseEntity implements Serializable {
    @TableId(type = IdType.ASSIGN_UUID)
    @Schema(description = "ID")
    private String id;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}


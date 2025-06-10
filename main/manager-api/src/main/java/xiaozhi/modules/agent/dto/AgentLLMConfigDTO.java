package xiaozhi.modules.agent.dto;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class AgentLLMConfigDTO {
    private JSONObject llmConfig;
    private String llmPrompt;
}

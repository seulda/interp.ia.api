package net.devgrr.interp.ia.api.work.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import net.devgrr.interp.ia.api.config.issue.IssueStatus;
import net.devgrr.interp.ia.api.config.issue.IssueType;
import net.devgrr.interp.ia.api.config.issue.Priority;
import net.devgrr.interp.ia.api.member.dto.MemberResponse;

@Schema(description = "연관 프로젝트 응답 객체")
public record ProjectRefResponse(
    @Schema(description = "프로젝트 ID") Long id,
    @Schema(description = "제목") String title,
    @Schema(description = "유형") IssueType type,
    @Schema(description = "상태") IssueStatus status,
    @Schema(description = "중요도") Priority priority,
    @Schema(description = "생성자") MemberResponse creator,
    @Schema(description = "담당자") Set<MemberResponse> assignee) {}

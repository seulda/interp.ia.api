package net.devgrr.interp.ia.api.work.history;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.devgrr.interp.ia.api.config.exception.BaseException;
import net.devgrr.interp.ia.api.config.exception.ErrorCode;
import net.devgrr.interp.ia.api.member.entity.Member;
import net.devgrr.interp.ia.api.work.history.entity.History;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class HistoryService {
  private final HistoryRepository historyRepository;

  public List<History> getHistoryByIssueId(Long issueId) {
    return historyRepository.findAllByIssueId(issueId);
  }

  @Transactional
  //  public void setHistory(History history) throws BaseException {
  //    try {
  //      historyRepository.save(history);
  //    } catch (Exception e) {
  //      throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
  //    }
  //  }
  public void setHistory(
      Long issueId, String beforeValue, String afterValue, String fieldName, Member modifier)
      throws BaseException {
    try {
      historyRepository.save(
          History.builder()
              .issueId(issueId)
              .beforeValue(beforeValue)
              .afterValue(afterValue)
              .fieldName(fieldName)
              .modifier(modifier)
              .build());
    } catch (Exception e) {
      throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }
}

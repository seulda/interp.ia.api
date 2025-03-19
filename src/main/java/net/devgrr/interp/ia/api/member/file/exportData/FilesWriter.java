package net.devgrr.interp.ia.api.member.file.exportData;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devgrr.interp.ia.api.config.exception.BaseException;
import net.devgrr.interp.ia.api.config.exception.ErrorCode;
import net.devgrr.interp.ia.api.member.MemberRepository;
import net.devgrr.interp.ia.api.member.entity.Member;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilesWriter {
  private final MemberRepository memberRepository;
  private final EntityManager entityManager;

  public void filesWriter(boolean header, List<String> cols, String filePath, String dataFormat)
      throws IOException, BaseException {
    if (filePath.endsWith(".csv")) {
      csvWriter(filePath, header, cols);
    } else if (filePath.endsWith(".xlsx")) {
      exelWriter(filePath, header, cols, dataFormat, true);
    } else if (filePath.endsWith(".xls")) {
      exelWriter(filePath, header, cols, dataFormat, false);
    } else {
      throw new BaseException(ErrorCode.INVALID_INPUT_VALUE, "허용되지 않은 파일 확장자입니다.");
    }
  }

  private void exelWriter(
      String filePath, boolean header, List<String> cols, String dataFormat, boolean isXlsx)
      throws IOException {
    List<String> fieldNames = new ArrayList<>();
    File file = new File(filePath);
    if (!file.exists()) {
      log.info("file does not exist: {}", filePath);
    }

    ExelStreamWriter writer = new ExelStreamWriter();
    writer.setFile(file);
    writer.setHeader(header);
    writer.setXlsx(isXlsx);
    if (cols == null) {
      fieldNames.addAll(
          Arrays.stream(Member.class.getDeclaredFields()).map(Field::getName).toList());
      fieldNames.addAll(
          Arrays.stream(Member.class.getSuperclass().getDeclaredFields())
              .map(Field::getName)
              .toList());
      writer.setFieldNames(fieldNames.toArray(new String[0]));
    } else {
      fieldNames.addAll(cols);
      writer.setFieldNames(fieldNames.toArray(new String[0]));
    }
    writer.setDataFormat(dataFormat);
    List<List<Object>> dataList = getData(cols);
    List<Map<String, Object>> data = new ArrayList<>();
    for (List<Object> objects : dataList) {
      Map<String, Object> map = new HashMap<>();
      for (int i = 0; i < objects.size(); i++) {
        map.put(fieldNames.get(i), objects.get(i));
      }
      data.add(map);
    }

    writer.write(data);
  }

  private List<List<Object>> getData(List<String> cols) {
    List<List<Object>> data = new ArrayList<>();

    if (cols == null || cols.isEmpty()) {
      List<Member> members = new ArrayList<>();
      members.addAll(memberRepository.findAll());
      for (Member member : members) {
        List<Object> row = getMemberRow(member);
        data.add(row);
      }
    } else {
      List<String> sql = new ArrayList<>();
      for (String col : cols) {
        sql.add("m." + col);
      }
      String query = "select " + String.join(", ", sql) + " from Member m";
      List<Object[]> rawData = entityManager.createQuery(query).getResultList();

      data = rawData.stream()
              .map(Arrays::asList)
              .collect(Collectors.toList());
    }
    return data;
  }

  private void csvWriter(String filePath, boolean header, List<String> cols) throws IOException {
    List<List<Object>> data = getData(cols);
    BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filePath), false));

    if (header) {
      if (cols == null) {
        String memberField =
                Arrays.stream(Member.class.getDeclaredFields())
                        .map(Field::getName)
                        .collect(Collectors.joining(","));
        String baseField =
                Arrays.stream(Member.class.getSuperclass().getDeclaredFields())
                        .map(Field::getName)
                        .collect(Collectors.joining(","));
        bw.write(memberField + "," + baseField);
        bw.newLine();
      } else {
        bw.write(String.join(",", cols));
        bw.newLine();
      }
    }
    for (List<Object> datum : data) {
      bw.write(datum.stream().map(obj -> Objects.toString(obj, "")).collect(Collectors.joining(",")));
      bw.newLine();
    }
    bw.flush();
    bw.close();
  }

  private List<Object> getMemberRow(Member member) {
    List<Object> row = new ArrayList<>();

    row.add(member.getId());
    row.add(member.getEmail());
    row.add(member.getPassword());
    row.add(member.getName());
    row.add(member.getImage());
    row.add(member.getPosition());
    row.add(member.getDepartment());
    row.add(member.getJob());
    row.add(member.getPhone());
    row.add(member.getRole());
    row.add(member.getRefreshToken());
    row.add(member.getIsActive());
    row.add(member.getCreatedDate());
    row.add(member.getUpdatedDate());

    return row;
  }
}

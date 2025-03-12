package net.devgrr.interp.ia.api.config.file;

import jakarta.persistence.EntityManagerFactory;
import net.devgrr.interp.ia.api.member.entity.Member;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaCursorReader {
    @Bean
    public JpaCursorItemReader<Member> jpaCursorItemReader(EntityManagerFactory emf) {
        JpaCursorItemReader<Member> reader = new JpaCursorItemReader<>();
        reader.setEntityManagerFactory(emf);
        reader.setQueryString("select m from Member m");

        return reader;
    }
}

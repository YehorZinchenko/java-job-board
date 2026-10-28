package com.zinchenko.jobboard;

import com.zinchenko.jobboard.job.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:jobtests;DB_CLOSE_DELAY=-1", "spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureMockMvc
class JobApiTests {
    @Autowired MockMvc mvc;
    @Autowired JobRepository repository;
    @Autowired ObjectMapper mapper;

    private static final String JOB = """
        {"title":" Junior Java Developer ","company":"North Studio","location":"Warsaw",
         "description":"Build useful Java services","experienceYears":0}
        """;

    @BeforeEach void clearDatabase() { repository.deleteAll(); }

    private long createJob() throws Exception {
        String response = mvc.perform(post("/api/jobs").contentType(MediaType.APPLICATION_JSON).content(JOB))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title").value("Junior Java Developer"))
                .andReturn().getResponse().getContentAsString();
        return mapper.readTree(response).get("id").asLong();
    }

    @Test void createsReadsUpdatesAndDeletesJob() throws Exception {
        long id = createJob();
        assertThat(repository.findById(id)).isPresent();
        mvc.perform(get("/api/jobs/" + id)).andExpect(status().isOk());
        mvc.perform(put("/api/jobs/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(JOB.replace("Warsaw", "Remote")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.location").value("Remote"));
        mvc.perform(delete("/api/jobs/" + id)).andExpect(status().isNoContent());
        assertThat(repository.findById(id)).isEmpty();
        mvc.perform(get("/api/jobs/" + id)).andExpect(status().isNotFound());
    }

    @Test void searchesCaseInsensitivelyByTitleAndCompany() throws Exception {
        createJob();
        for (String query : new String[]{"JAVA", "north", "  Java  ", ""}) {
            mvc.perform(get("/api/jobs").param("q", query))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        }
        mvc.perform(get("/api/jobs").param("q", "Python")).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/jobs").param("q", "%")).andExpect(jsonPath("$.length()").value(0));
    }

    @Test void rejectsInvalidInputWithoutWritingToDatabase() throws Exception {
        for (String invalid : new String[]{JOB.replace(" Junior Java Developer ", "  "),
                JOB.replace(":0", ":-1"),
                JOB.replace(":0", ":null"),
                JOB.replace("North Studio", "x".repeat(101))}) {
            mvc.perform(post("/api/jobs").contentType(MediaType.APPLICATION_JSON).content(invalid))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors").exists());
        }
        assertThat(repository.count()).isZero();
    }

    @Test void rejectsInvalidUpdateAndPreservesOriginal() throws Exception {
        long id = createJob();
        mvc.perform(put("/api/jobs/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(JOB.replace("Warsaw", " "))).andExpect(status().isBadRequest());
        mvc.perform(get("/api/jobs/" + id)).andExpect(jsonPath("$.location").value("Warsaw"));
    }

    @Test void reportsMissingJobsForAllOperations() throws Exception {
        mvc.perform(get("/api/jobs/999999")).andExpect(status().isNotFound());
        mvc.perform(put("/api/jobs/999999").contentType(MediaType.APPLICATION_JSON).content(JOB)).andExpect(status().isNotFound());
        mvc.perform(delete("/api/jobs/999999")).andExpect(status().isNotFound());
    }

    @Test void returnsBadRequestForMalformedJsonAndId() throws Exception {
        mvc.perform(post("/api/jobs").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/jobs/not-a-number")).andExpect(status().isBadRequest());
    }

    @Test void servesBrowserInterface() throws Exception {
        mvc.perform(get("/index.html")).andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Zinchenko")));
    }
}

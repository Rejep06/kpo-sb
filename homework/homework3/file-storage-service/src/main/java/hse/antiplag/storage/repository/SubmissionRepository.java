package hse.antiplag.storage.repository;

import hse.antiplag.storage.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}

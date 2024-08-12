package stagev1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import stagev1.models.CVData;

public interface CvRepository extends JpaRepository<CVData, Integer> {

}

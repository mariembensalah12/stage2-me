package stagev1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import stagev1.models.Forms;

public interface FormRepository extends JpaRepository<Forms, Integer>  {

}

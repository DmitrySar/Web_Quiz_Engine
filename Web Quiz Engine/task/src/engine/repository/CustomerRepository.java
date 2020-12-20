package engine.repository;

import engine.domain.Customer;
import engine.domain.Quiz;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer> {
    public Optional<Customer> findByEmail(String email);
    public Customer findByQuizzesContains(Quiz quiz);
    public void deleteByQuizzesEquals(Quiz quiz);
}

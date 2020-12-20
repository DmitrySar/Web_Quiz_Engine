package engine.repository;

import engine.domain.CompleteQuizzes;
import engine.domain.Customer;
import engine.domain.Quiz;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CompleteQuizzesRepository extends PagingAndSortingRepository<CompleteQuizzes, Integer> {
    public List<CompleteQuizzes> findCompleteQuizzesByCustomerListContains(Customer customer, Sort sort);
    public List<CompleteQuizzes> findCompleteQuizzesByCustomerListContains(Customer customer);
    public List<CompleteQuizzes> findAll();

}

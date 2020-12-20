# Пример разбивки на страницы и сортировки Spring загрузки
## Научитесь запрашивать и отображать только фрагменты данных из базы данных, используя разбиение на страницы и сортировку входных данных и параметров запроса в приложениях Spring Boot и Spring Data.

Пейджинг и сортировка в основном требуются, когда мы отображаем данные домена в табличном формате в пользовательском интерфейсе.

Разбивка состоят из двух полей - размер страницы и номер страницы . Сортировка выполняется по одному из нескольких полей в таблице.

### 1. Организация JPA
В этом посте мы берем пример EmployeeEntityкласса. Каждый экземпляр сущности представляет собой запись сотрудника в базе данных.

```code java
EmployeeEntity.java
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
 
@Entity
@Table(name="TBL_EMPLOYEES")
public class EmployeeEntity {
 
    @Id
    @GeneratedValue
    private Long id;
     
    @Column(name="first_name")
    private String firstName;
     
    @Column(name="last_name")
    private String lastName;
     
    @Column(name="email", nullable=false, length=200)
    private String email;
     
    //Setters and getters
 
    @Override
    public String toString() {
        return "EmployeeEntity [id=" + id + ", firstName=" + firstName + 
                ", lastName=" + lastName + ", email=" + email   + "]";
    }
}
```
### 2. PagingAndSortingRepository
PagingAndSortingRepository - это расширение, CrudRepositoryпредоставляющее дополнительные методы для извлечения сущностей с использованием абстракции разбивки на страницы и сортировки. Он предоставляет два метода:

Page findAll (Pageable pageable) - возвращает набор Pageсущностей, удовлетворяющих ограничению разбиения на страницы, предусмотренному в Pageableобъекте.
Iterable findAll (Sort sort) - возвращает все сущности, отсортированные по заданным параметрам. Пейджинг здесь не применяется.
```code java
EmployeeRepository.java
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
 
import com.howtodoinjava.demo.entity.EmployeeEntity;
 
@Repository
public interface EmployeeRepository 
        extends PagingAndSortingRepository<EmployeeEntity, Long> {
 
}
```
### 3. Принятие параметров пейджинга и сортировки.
Как правило, параметры разбиения на страницы и сортировки являются необязательными и, следовательно, являются частью URL-адреса запроса в качестве параметров запроса . Если какой-либо API поддерживает разбиение по страницам и сортировку, ВСЕГДА предоставляйте значения по умолчанию для его параметров - для использования, когда клиент не выбирает указать какие-либо предпочтения разбиения по страницам или сортировки.

Значения разбивки на страницы и сортировки по умолчанию должны быть четко задокументированы в документации API. В пользовательском интерфейсе эти значения по умолчанию могут быть выделены отдельными цветами.

Значения номеров страниц начинаются с 0. Таким образом, в пользовательском интерфейсе, если вы показываете номер страницы от 1, не забудьте вычесть «1» при выборке записей.

В приведенном ниже контроллере Spring mvc мы принимаем параметры разбиения на страницы и сортировку, используя параметры запроса pageNo , pageSize и sortBy . Кроме того, по умолчанию '10'сотрудники будут извлекаться из базы данных по номеру страницы '0', а записи сотрудников будут отсортированы по 'id'полю.
```code java
EmployeeController.java
@RestController
@RequestMapping("/employees")
public class EmployeeController 
{
    @Autowired
    EmployeeService service;
 
    @GetMapping
    public ResponseEntity<List<EmployeeEntity>> getAllEmployees(
                        @RequestParam(defaultValue = "0") Integer pageNo, 
                        @RequestParam(defaultValue = "10") Integer pageSize,
                        @RequestParam(defaultValue = "id") String sortBy) 
    {
        List<EmployeeEntity> list = service.getAllEmployees(pageNo, pageSize, sortBy);
 
        return new ResponseEntity<List<EmployeeEntity>>(list, new HttpHeaders(), HttpStatus.OK); 
    }
}
```
Чтобы выполнить разбиение на страницы и / или сортировку, мы должны создать org.springframework.data.domain.Pageableили org.springframework.data.domain.Sortпередать экземпляры findAll()методу.

```code java
EmployeeService.java
@Service
public class EmployeeService 
{
    @Autowired
    EmployeeRepository repository;
     
    public List<EmployeeEntity> getAllEmployees(Integer pageNo, Integer pageSize, String sortBy)
    {
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
 
        Page<EmployeeEntity> pagedResult = repository.findAll(paging);
         
        if(pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<EmployeeEntity>();
        }
    }
}
```
### 4. Методы разбивки на страницы и сортировки.
#### 4.1. Пейджинг БЕЗ сортировки
Чтобы применить только разбиение на страницы в результирующем наборе, мы создадим Pageableобъект без какой-либо Sortинформации.

Pageable paging = PageRequest.of(pageNo, pageSize);
 
Page<EmployeeEntity> pagedResult = repository.findAll(paging); 
#### 4.2. Пейджинг с сортировкой
Чтобы применить только разбиение на страницы в наборе результатов, мы создадим Pageableобъект с желаемым Sortименем столбца.

Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("email"));
 
Page<EmployeeEntity> pagedResult = repository.findAll(paging);
По умолчанию записи упорядочены по убыванию . Чтобы выбрать восходящий порядок, используйте .ascending()метод.

Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("email").ascending()); 
 
Page<EmployeeEntity> pagedResult = repository.findAll(paging); 
#### 4.3. Только сортировка
Если нет необходимости листать страницы, а требуется только сортировка, мы можем создать Sortдля этого объект.

Sort sortOrder = Sort.by("email"); 
 
List<EmployeeEntity> list = repository.findAll(sortOrder);
Если мы хотим применить сортировку к нескольким столбцам или сгруппировать по сортировке , это также возможно, создав Sortс помощью простых шагов шаблона построителя .

Sort emailSort = Sort.by("email"); 
Sort firstNameSort = Sort.by("first_name"); 
 
Sort groupBySort = emailSort.and(firstNameSort);
 
List<EmployeeEntity> list = repository.findAll(groupBySort);
### 5. Разница между страницей и фрагментом
#### 5.1. Страница
findAll(Pageable pageable)Метод по умолчанию возвращает Pageобъект. PageОбъект содержит много дополнительных полезных сведений, кроме всего списка сотрудников в текущей странице.

Например, у Pageобъекта есть общее количество страниц , количество, current pageа также то, является ли текущая страница первой или последней страницей.

Поиск общего количества страниц вызывает дополнительный запрос count (), что приводит к дополнительным накладным расходам. Будьте уверены, когда вы его используете.

#### 5.2. Ломтик
Sliceочень похож на Page, за исключением того, что он не предоставляет общее количество страниц в базе данных. Это помогает повысить производительность, когда нам не нужно отображать общее количество страниц в пользовательском интерфейсе.

Обычно Sliceиспользуется в случае, если навигация состоит из ссылок « Следующая страница» и «Предыдущая страница» .

Для использования Sliceмы реализовали наши собственные пользовательские методы.

```code java
EmployeeRepository.java
public interface EmployeeRepository extends CrudRepository<EmployeeEntity, Long> 
{
    public Slice<EmployeeEntity> findByFirstName(String firstName, Pageable pageable);
}
```
Помните, что мы используем PagingAndSortingRepositoryтип возвращаемого значения по умолчанию Page.
```code java
Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by("email").descending()); 
 
Slice<EmployeeEntity> slicedResult = repository.findByFirstName("alex", paging); 
 
List<EmployeeEntity> employeeList = slicedResult.getContent();
```
### 6. Демонстрация загрузки страниц и сортировки Spring
В этой демонстрации номер страницы по умолчанию - 0, размер страницы - 10, а столбец сортировки по умолчанию - id.

Теперь вызовите эти URL-адреса один за другим и наблюдайте за результатами.

http: // localhost: 8080 / сотрудники? pageSize = 5
http: // localhost: 8080 / сотрудники? pageSize = 5 & pageNo = 1
http: // localhost: 8080 / сотрудники? pageSize = 5 & pageNo = 2
http: // localhost: 8080 / сотрудники? pageSize = 5 & pageNo = 1 & sortBy = электронная почта
http: // localhost: 8080 / сотрудники? pageSize = 5 & pageNo = 1 & sortBy = firstName
Напишите мне в комментариях свои вопросы, связанные с разбивкой на страницы и сортировкой с помощью Spring Data JPA .
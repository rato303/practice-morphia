import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.annotations.*;
import dev.morphia.query.Query;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class QuickTour {

  private QuickTour() {
  }

  public static void main(String[] args) {
    String rawConnectionString = args[0];
    ConnectionString connectionString = new ConnectionString(rawConnectionString);

    MongoClient client = MongoClients.create(connectionString);
    final Datastore datastore = Morphia.createDatastore(client, "morphia_example");

    // tell morphia where to find your classes
    // can be called multiple times with different packages or classes
    datastore.getMapper().mapPackage("dev.morphia.example");

    // create the Datastore connecting to the database running on the default port on the local host
//    datastore.getDatabase().drop();
    datastore.ensureIndexes();

    final Employee elmer = new Employee("Elmer Fudd", 50000.0);
    datastore.save(elmer);

    final Employee daffy = new Employee("Daffy Duck", 40000.0);
    datastore.save(daffy);

    final Employee pepe = new Employee("Pepé Le Pew", 25000.0);
    datastore.save(pepe);

    elmer.getDirectReports().add(daffy);
    elmer.getDirectReports().add(pepe);

    datastore.save(elmer);

    Query<Employee> query = datastore.find(Employee.class);
    final long employees = query.count();

//    assertEquals(employees, 3);

//    long underpaid = datastore.find(Employee.class)
//      .filter(lte("salary", 30000))
//      .count();
//    assertEquals(underpaid, 1);

//    final Query<Employee> underPaidQuery = datastore.find(Employee.class)
//      .filter(lte("salary", 30000));
//    final UpdateResult results = underPaidQuery.update(inc("salary", 10000))
//      .execute();

//    assertEquals(results.getModifiedCount(), 1);

//    datastore.find(Employee.class)
//      .filter(gt("salary", 100000))
//      .findAndDelete();
  }
}

@Entity("employees")
@Indexes(@Index(options = @IndexOptions(name = "salary"), fields = @Field("salary")))
class Employee {
  @Id
  private ObjectId id;
  private String name;
  private Integer age;
  @Reference
  private Employee manager;
  @Reference
  private List<Employee> directReports = new ArrayList<>();
  @Property("wage")
  private Double salary;

  Employee() {
  }

  Employee(String name, Double salary) {
    this.name = name;
    this.salary = salary;
  }

  public List<Employee> getDirectReports() {
    return directReports;
  }

  public void setDirectReports(List<Employee> directReports) {
    this.directReports = directReports;
  }

  public ObjectId getId() {
    return id;
  }

  public Employee getManager() {
    return manager;
  }

  public void setManager(Employee manager) {
    this.manager = manager;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getSalary() {
    return salary;
  }

  public void setSalary(Double salary) {
    this.salary = salary;
  }
}

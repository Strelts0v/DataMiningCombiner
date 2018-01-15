package evm.dmc.api.model.account;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import evm.dmc.api.model.ProjectModel;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Entity
@Data
@Table(name="ACCOUNT")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@EqualsAndHashCode(exclude={"projects"})
@ToString(exclude="projects")
@Slf4j
public class Account implements Serializable {
	@Transient
	private static final String NOT_BLANK_MESSAGE = "{error.emptyField}";
	@Transient
	private static final String EMAIL_MESSAGE = "{error.email}";
	@Transient
	private static final String USERNAME_SIZE_MESSAGE = "{error.username.size}";

	private static final long serialVersionUID = 4198630702609693622L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Long id;

	@NotBlank(message = NOT_BLANK_MESSAGE)
	@Column(unique = true, nullable = false)
	@Size(min = 4, max = 16, message = USERNAME_SIZE_MESSAGE)
	private String userName;

	@JsonIgnore
	@NotBlank(message = Account.NOT_BLANK_MESSAGE)
	@Size(min = 5, max = 16)
	private String password;

	@Column(unique = true, nullable = false)
	@NotBlank(message = Account.NOT_BLANK_MESSAGE)
	@Email(message = Account.EMAIL_MESSAGE)
	private String email;

	private String firstName;

	private String lastName;
	
	@Enumerated(EnumType.STRING)
	@Setter(AccessLevel.PROTECTED)
	@NotNull
	protected Role role = Role.USER;
	
	@Setter(AccessLevel.NONE) 
	private Instant created = Instant.now();
	
	@OneToMany(mappedBy="account", fetch = FetchType.LAZY,
			orphanRemoval = true, cascade = CascadeType.ALL) //{CascadeType.REMOVE, CascadeType.PERSIST}, 
	private Set<ProjectModel> projects = new HashSet<>();
//	private List<ProjectModel> projects = new LinkedList<>();

    public Account() {
    }
    
    public Account(String name) {
    	this.userName = name;
    }

	public Account(String username, String password, String email, 
			String firstName, String lastName) {
		super();
		this.email = email;
		this.userName = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.created = Instant.now();
	}
	
	public Account(Account acc) {
//		this.id = acc.id;
		this.userName = acc.userName;
		this.created = acc.created;
		this.email = acc.email;
		this.firstName = acc.firstName;
		this.lastName = acc.lastName;
		this.password = acc.password;
		this.role = acc.role;
	}
	
	public Account addProject(ProjectModel project) {
		projects.add(project);
		project.setAccount(this);
		return this;
	}
	
	public void removeProject(ProjectModel project) {
		projects.remove(project);
		project.setAccount(null);
//		return this;
	}
	
	public void removeProjectByName(String name) {
////		for(ProjectModel proj: projects) {
//		for(Iterator<ProjectModel> iter =  projects.iterator(); iter.hasNext();) {
//			ProjectModel proj = iter.next();
//			if(proj.getName().equals(name)) {
//				projects.remove(proj);
//				log.debug("Project {} removed", name);
//			}
//		}
		projects.removeIf((proj) -> proj.getName().equals(name));
	}
	
	public void removeProjectsByNames(String names[]) {
//		projects.forEach((proj) -> {if( nameContainsOneOf(proj.getName(), names) ) removeProject(proj);});
		projects.removeIf((proj) -> nameContainsOneOf(proj.getName(), names));
	}
	
	private static boolean nameContainsOneOf(String name, String[] names) {
		return Arrays.stream(names).anyMatch(name :: contains);
	}

}
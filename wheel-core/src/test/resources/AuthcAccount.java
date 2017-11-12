package rebue.mincms.authc.po;
// Generated 2016-9-24 22:26:07 by Hibernate Tools 5.2.0.Beta1

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * AuthcAccount generated by hbm2java
 */
@Entity
@Table(name = "AUTHC_ACCOUNT", catalog = "mincms")
public class AuthcAccount implements java.io.Serializable {

	private long			id;
	private String			name;
	private String			code;
	private String			password;
	private boolean			enabled;
	private boolean			sys;
	private Set<AuthcRole>	authcRoles	= new HashSet<AuthcRole>(0);

	public AuthcAccount() {
	}

	public AuthcAccount(long id, String name, String code, String password, boolean enabled, boolean sys) {
		this.id = id;
		this.name = name;
		this.code = code;
		this.password = password;
		this.enabled = enabled;
		this.sys = sys;
	}

	public AuthcAccount(long id, String name, String code, String password, boolean enabled, boolean sys, Set<AuthcRole> authcRoles) {
		this.id = id;
		this.name = name;
		this.code = code;
		this.password = password;
		this.enabled = enabled;
		this.sys = sys;
		this.authcRoles = authcRoles;
	}

	@Id

	@Column(name = "ID", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "NAME", nullable = false, length = 50)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "CODE", nullable = false, length = 50)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Column(name = "PASSWORD", nullable = false, length = 50)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "ENABLED", nullable = false)
	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = "SYS", nullable = false)
	public boolean isSys() {
		return this.sys;
	}

	public void setSys(boolean sys) {
		this.sys = sys;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "AUTHC_ACCOUNT_ROLE", catalog = "mincms", joinColumns = {
			@JoinColumn(name = "ACCOUNT_ID", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "ROLE_ID", nullable = false, updatable = false) })
	public Set<AuthcRole> getAuthcRoles() {
		return this.authcRoles;
	}

	public void setAuthcRoles(Set<AuthcRole> authcRoles) {
		this.authcRoles = authcRoles;
	}

}

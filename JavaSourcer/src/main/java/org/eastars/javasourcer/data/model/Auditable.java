package org.eastars.javasourcer.data.model;

import java.time.LocalDateTime;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditable {

	@CreationTimestamp
    protected LocalDateTime creationDate;

	public LocalDateTime getCreationDate() {
		return creationDate;
	}
	
}

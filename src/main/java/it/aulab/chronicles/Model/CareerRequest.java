package it.aulab.chronicles.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "career_request") 

public class CareerRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 1000)
    private String body;
    @Column
    private boolean isChecked;
    @Column(name = "is_reviewed", nullable = false)
    private boolean isReviewed;

    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToOne
    @JoinColumn(name = "role_id")
    private Role role;
    
    
    public boolean getIsChecked() {
        return isChecked;
    }
    
    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean getIsReviewed() {
        return isReviewed;
    }
    
    public void setIsReviewed(boolean isReviewed) {
        this.isReviewed = isReviewed;
    }
   



}

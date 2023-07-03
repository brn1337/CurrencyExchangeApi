package pl.bombit.currencyexchangeapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bombit.currencyexchangeapi.repository.entity.AccountEntity;
@Repository
public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {
    AccountEntity findByFirstNameAndLastName(String firstName, String lastName);
    AccountEntity findByAccId(String accId);
}

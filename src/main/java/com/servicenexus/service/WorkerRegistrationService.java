
package com.servicenexus.service;

import com.servicenexus.dto.WorkerRegistrationRequest;
import com.servicenexus.dto.WorkerRegistrationResponse;
import com.servicenexus.entity.Customer;
import com.servicenexus.entity.WorkerAccount;
import com.servicenexus.model.WorkerProfile;
import com.servicenexus.repository.CustomerRepository;
import com.servicenexus.repository.WorkerAccountRepository;
import com.servicenexus.repository.WorkerProfileRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkerRegistrationService {

    private final WorkerAccountRepository accountRepository;
    private final WorkerProfileRepository profileRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public WorkerRegistrationService(
            WorkerAccountRepository accountRepository,
            WorkerProfileRepository profileRepository,
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {

        this.accountRepository = accountRepository;
        this.profileRepository = profileRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public WorkerRegistrationResponse register(
            WorkerRegistrationRequest request) {

        String email = request.email().trim().toLowerCase();

        if (email.equals("admin")
                || accountRepository.existsByEmailIgnoreCase(email)
                || customerRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "This email is already registered.");
        }

        WorkerProfile profile = new WorkerProfile();
        profile.setName(request.name().trim());
        profile.setPhone(request.phone().trim());
        profile.setSkills(request.skills().trim());
        profile.setLocation(request.location().trim());
        profile.setDescription(request.description());

        WorkerProfile savedProfile = profileRepository.save(profile);

        WorkerAccount account = new WorkerAccount();
        account.setEmail(email);
        account.setPasswordHash(
                passwordEncoder.encode(request.password()));
        account.setWorkerProfile(savedProfile);
        account.setVerificationStatus("PENDING");
        WorkerAccount savedAccount = accountRepository.save(account);

        return new WorkerRegistrationResponse(
                savedAccount.getId(),
                savedProfile.getId(),
                savedProfile.getName(),
                savedAccount.getEmail());
    }
}

package it.polito.verefoo.rest.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.polito.verefoo.SubstrateId;
import it.polito.verefoo.rest.spring.repository.SubstrateRepository;

@Service
public class SubstrateService {

    @Autowired
    SubstrateRepository substrateRepository;

    public Long createSubstrate() {
        return substrateRepository.save(new SubstrateId()).getId();
    }
}

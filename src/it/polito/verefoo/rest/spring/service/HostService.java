package it.polito.verefoo.rest.spring.service;

import org.springframework.beans.factory.annotation.Autowired;

import it.polito.verefoo.rest.spring.repository.HostRepository;

public class HostService {
    
    @Autowired
    HostRepository hostRepository;
    
}

package br.com.demo.services;

import br.com.demo.domain.dtos.DemoSaveDTO;
import br.com.demo.domain.entities.Demo;
import br.com.demo.domain.entities.DemoCache;
import br.com.demo.repositories.CacheRepository;
import br.com.demo.repositories.DemoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Service
public class DemoService {

    private final DemoRepository demoRepository;
    private final CacheRepository cacheRepository;

    public void save(DemoSaveDTO demoSaveDTO) {
        Demo entity = new Demo();
        entity.setId(UUID.randomUUID());
        entity.setNome(demoSaveDTO.getNome());

        demoRepository.save(entity);

        DemoCache cache = new DemoCache();
        cache.setId(entity.getId());
        cache.setNome(entity.getNome());
        cache.setExpirationTime(Instant.now().getEpochSecond() + 300);

        cacheRepository.save(cache);
    }

    public Demo getById(UUID id) {
        DemoCache cache = cacheRepository.getById(id);
        if (cache != null) {
            return new Demo(cache.getId(), cache.getNome());
        }

        System.out.println("🟡 Cache miss para o ID: " + id);

        Demo entity = demoRepository.getById(id);

        if (entity != null) {

            DemoCache newCache = new DemoCache();
            newCache.setId(entity.getId());
            newCache.setNome(entity.getNome());
            newCache.setExpirationTime(Instant.now().getEpochSecond() + 300);
            cacheRepository.save(newCache);
            System.out.println("✅ Dados salvos no cache para o ID: " + entity.getId());
        }

        return entity;
    }


}

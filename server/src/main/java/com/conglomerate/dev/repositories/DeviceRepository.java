package com.conglomerate.dev.repositories;

import com.conglomerate.dev.models.Device;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceRepository extends JpaRepository<Device, Integer> {
}

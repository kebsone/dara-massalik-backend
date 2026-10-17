package com.daramassalik.backend.auth;

import java.util.List;

public record UserSummaryResponse(Long id, String firstName, String lastName, String phone, String email,
                                  boolean active, List<Role> roles) {}

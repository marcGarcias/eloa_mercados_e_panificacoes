import { User, UserRole, UserStatus } from '../../app/models/user.model';
import { LoginResponse, BootstrapUserResponse } from '../../app/services/auth.service';

export function createMockUser(overrides: Partial<User> = {}): User {
  return {
    id: 'usr-123456',
    userCode: 'ADM001',
    name: 'Admin Teste',
    role: 'ADMIN',
    status: 'ACTIVE',
    lastLoginAt: '2026-09-12T10:00:00Z',
    ...overrides
  };
}

export function createMockSuperAdmin(overrides: Partial<User> = {}): User {
  return createMockUser({
    id: 'usr-super-01',
    userCode: 'SUPER001',
    name: 'Proprietário Geral',
    role: 'SUPER_ADMIN',
    ...overrides
  });
}

export function createMockEditor(overrides: Partial<User> = {}): User {
  return createMockUser({
    id: 'usr-editor-01',
    userCode: 'EDT001',
    name: 'Editor Catálogo',
    role: 'EDITOR',
    ...overrides
  });
}

export function createMockLoginResponse(token = 'mocked-jwt-token-xyz'): LoginResponse {
  return {
    accessToken: token
  };
}

export function createMockBootstrapResponse(): BootstrapUserResponse {
  return {
    id: 'usr-bootstrap-01',
    name: 'Primeiro Admin',
    userCode: 'ADM000',
    role: 'SUPER_ADMIN',
    status: 'ACTIVE'
  };
}


import { describe, it, expect, vi, beforeEach } from 'vitest';
import {
  validateProductImage,
  formatBytes,
  compressAndConvertToWebp,
  MAX_IMAGE_SIZE_BYTES,
} from './image-compressor.util';

describe('image-compressor.util', () => {
  describe('validateProductImage()', () => {
    it('deve rejeitar arquivo nulo ou indefinido', () => {
      expect(validateProductImage(null).valid).toBe(false);
      expect(validateProductImage(undefined).valid).toBe(false);
    });

    it('deve aceitar formatos suportados WebP, PNG e JPEG', () => {
      const webpFile = new File(['dummy'], 'foto.webp', { type: 'image/webp' });
      const pngFile = new File(['dummy'], 'foto.png', { type: 'image/png' });
      const jpgFile = new File(['dummy'], 'foto.jpg', { type: 'image/jpeg' });

      expect(validateProductImage(webpFile).valid).toBe(true);
      expect(validateProductImage(pngFile).valid).toBe(true);
      expect(validateProductImage(jpgFile).valid).toBe(true);
    });

    it('deve rejeitar formatos não permitidos como GIF, SVG, PDF ou scripts', () => {
      const gifFile = new File(['dummy'], 'anim.gif', { type: 'image/gif' });
      const svgFile = new File(['dummy'], 'vector.svg', { type: 'image/svg+xml' });
      const pdfFile = new File(['dummy'], 'doc.pdf', { type: 'application/pdf' });
      const exeFile = new File(['dummy'], 'virus.exe', { type: 'application/x-msdownload' });

      expect(validateProductImage(gifFile).valid).toBe(false);
      expect(validateProductImage(svgFile).valid).toBe(false);
      expect(validateProductImage(pdfFile).valid).toBe(false);
      expect(validateProductImage(exeFile).valid).toBe(false);
    });

    it('deve rejeitar arquivos que excedam 10MB', () => {
      const hugeFile = new File([''], 'huge.webp', { type: 'image/webp' });
      Object.defineProperty(hugeFile, 'size', { value: MAX_IMAGE_SIZE_BYTES + 1024 });

      const result = validateProductImage(hugeFile);
      expect(result.valid).toBe(false);
      expect(result.error).toContain('10MB');
    });
  });

  describe('formatBytes()', () => {
    it('deve formatar bytes em unidades legíveis', () => {
      expect(formatBytes(0)).toBe('0 B');
      expect(formatBytes(500)).toBe('500 B');
      expect(formatBytes(1024)).toBe('1 KB');
      expect(formatBytes(1024 * 1024 * 4.2)).toBe('4.2 MB');
    });
  });
});

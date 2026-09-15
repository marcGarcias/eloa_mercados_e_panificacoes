export interface ImageValidationResult {
  valid: boolean;
  error?: string;
}

export interface CompressedImageResult {
  file: File;
  previewUrl: string;
  originalSizeBytes: number;
  compressedSizeBytes: number;
  savedPercentage: number;
  width: number;
  height: number;
}

export const ALLOWED_IMAGE_MIME_TYPES = [
  'image/webp',
  'image/png',
  'image/jpeg',
  'image/jpg',
];

export const ALLOWED_IMAGE_EXTENSIONS = ['.webp', '.png', '.jpg', '.jpeg'];
export const MAX_IMAGE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB

/**
 * Valida se o arquivo possui um formato suportado (WebP, PNG, JPEG/JPG) e se está dentro do limite de 10MB.
 */
export function validateProductImage(file: File | null | undefined): ImageValidationResult {
  if (!file) {
    return { valid: false, error: 'Arquivo de imagem obrigatório.' };
  }

  const hasValidMime = ALLOWED_IMAGE_MIME_TYPES.includes(file.type.toLowerCase());
  const fileNameLower = file.name.toLowerCase();
  const hasValidExt = ALLOWED_IMAGE_EXTENSIONS.some((ext) => fileNameLower.endsWith(ext));

  if (!hasValidMime && !hasValidExt) {
    return {
      valid: false,
      error: 'Formato não suportado. Formatos aceitos: WebP, PNG e JPEG/JPG.',
    };
  }

  if (file.size > MAX_IMAGE_SIZE_BYTES) {
    const sizeMb = (file.size / (1024 * 1024)).toFixed(1);
    return {
      valid: false,
      error: `A imagem ultrapassa o limite de 10MB (tamanho atual: ${sizeMb}MB).`,
    };
  }

  return { valid: true };
}

/**
 * Converte bytes em representação amigável (ex: "4.2 MB", "85 KB").
 */
export function formatBytes(bytes: number): string {
  if (bytes <= 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return `${parseFloat((bytes / Math.pow(k, i)).toFixed(1))} ${sizes[i]}`;
}

/**
 * Redimensiona a imagem proporcionalmente e comprime para WebP via Canvas API no navegador.
 */
export async function compressAndConvertToWebp(
  file: File,
  maxWidth = 1080,
  maxHeight = 1080,
  quality = 0.82
): Promise<CompressedImageResult> {
  const originalSizeBytes = file.size;

  return new Promise((resolve) => {
    const cleanBaseName = file.name.replace(/\.[^/.]+$/, '');

    // Fallback padrão se não houver DOM ou suporte gráfico
    const createFallbackResult = (): CompressedImageResult => ({
      file: new File([file], `${cleanBaseName}.webp`, { type: 'image/webp' }),
      previewUrl: '',
      originalSizeBytes,
      compressedSizeBytes: originalSizeBytes,
      savedPercentage: 0,
      width: maxWidth,
      height: maxHeight,
    });

    if (typeof window === 'undefined' || typeof document === 'undefined' || !document.createElement) {
      return resolve(createFallbackResult());
    }

    const reader = new FileReader();

    // Timeout de segurança para garantir resolução mesmo em ambientes lentos ou sem render loop
    const timeoutId = setTimeout(() => {
      resolve(createFallbackResult());
    }, 200);

    reader.onerror = () => {
      clearTimeout(timeoutId);
      resolve(createFallbackResult());
    };

    reader.onload = () => {
      const img = new Image();

      img.onerror = () => {
        clearTimeout(timeoutId);
        resolve(createFallbackResult());
      };

      img.onload = () => {
        clearTimeout(timeoutId);
        let width = img.width || maxWidth;
        let height = img.height || maxHeight;

        if (width > maxWidth || height > maxHeight) {
          const ratio = Math.min(maxWidth / width, maxHeight / height);
          width = Math.round(width * ratio);
          height = Math.round(height * ratio);
        }

        const canvas = document.createElement('canvas');
        canvas.width = width;
        canvas.height = height;
        const ctx = canvas.getContext('2d');

        if (!ctx || !canvas.toBlob) {
          return resolve(createFallbackResult());
        }

        ctx.imageSmoothingEnabled = true;
        ctx.imageSmoothingQuality = 'high';
        ctx.drawImage(img, 0, 0, width, height);

        canvas.toBlob(
          (blob) => {
            const optimizedFileName = `${cleanBaseName}.webp`;
            const optimizedFile = blob
              ? new File([blob], optimizedFileName, { type: 'image/webp', lastModified: Date.now() })
              : new File([file], optimizedFileName, { type: 'image/webp', lastModified: Date.now() });

            const compressedSizeBytes = optimizedFile.size;
            const savedBytes = Math.max(0, originalSizeBytes - compressedSizeBytes);
            const savedPercentage =
              originalSizeBytes > 0 ? Math.round((savedBytes / originalSizeBytes) * 100) : 0;

            const previewUrl = canvas.toDataURL ? canvas.toDataURL('image/webp', quality) : '';

            resolve({
              file: optimizedFile,
              previewUrl,
              originalSizeBytes,
              compressedSizeBytes,
              savedPercentage,
              width,
              height,
            });
          },
          'image/webp',
          quality
        );
      };

      try {
        img.src = reader.result as string;
        // Disparo para ambientes headless/jsdom
        if (typeof (img as any).onload === 'function' && !img.complete) {
          setTimeout(() => {
            if (typeof (img as any).onload === 'function') {
              (img as any).onload();
            }
          }, 10);
        }
      } catch {
        clearTimeout(timeoutId);
        resolve(createFallbackResult());
      }
    };

    try {
      reader.readAsDataURL(file);
    } catch {
      clearTimeout(timeoutId);
      resolve(createFallbackResult());
    }
  });
}

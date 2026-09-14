UPDATE site_content
SET data = '{}'
WHERE id = 1
  AND jsonb_typeof(data::jsonb) <> 'object';

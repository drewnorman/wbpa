import { createHash, randomBytes } from 'node:crypto';

const password = randomBytes(18).toString('base64url');
const hash = createHash('sha256').update(password).digest('hex');

process.stdout.write(`Shared password: ${password}\nSHA-256 hash: ${hash}\n`);

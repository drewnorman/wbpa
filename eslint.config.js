import js from '@eslint/js';
import globals from 'globals';
import tseslint from 'typescript-eslint';

export default [
  { ignores: ['dist/**', 'node_modules/**', 'legacy/**'] },
  js.configs.recommended,
  ...tseslint.configs.recommended,
  {
    rules: {
      'array-callback-return': ['error', { allowImplicit: true }],
      'arrow-body-style': ['error', 'as-needed'],
      'consistent-return': 'error',
      curly: ['error', 'multi-line'],
      eqeqeq: ['error', 'always', { null: 'ignore' }],
      'no-duplicate-imports': ['error', { allowSeparateTypeImports: true }],
      'no-else-return': ['error', { allowElseIf: false }],
      'no-loop-func': 'error',
      'no-multi-assign': 'error',
      'no-param-reassign': [
        'error',
        { props: true, ignorePropertyModificationsFor: ['mount'] },
      ],
      'no-promise-executor-return': 'error',
      'no-return-assign': ['error', 'always'],
      'object-shorthand': [
        'error',
        'always',
        { ignoreConstructors: false, avoidQuotes: true },
      ],
      'prefer-arrow-callback': [
        'error',
        { allowNamedFunctions: false, allowUnboundThis: true },
      ],
      'prefer-destructuring': [
        'error',
        {
          VariableDeclarator: { array: false, object: true },
          AssignmentExpression: { array: true, object: false },
        },
        { enforceForRenamedProperties: false },
      ],
      'prefer-object-spread': 'error',
      'prefer-template': 'error',
    },
  },
  {
    files: ['**/*.ts'],
    rules: {
      '@typescript-eslint/consistent-type-imports': [
        'error',
        { prefer: 'type-imports', fixStyle: 'inline-type-imports' },
      ],
      '@typescript-eslint/no-shadow': 'error',
    },
  },
  {
    files: ['src/**/*.ts'],
    languageOptions: { globals: globals.browser },
  },
  {
    files: ['*.config.js', '*.config.ts'],
    languageOptions: { globals: globals.node },
  },
];

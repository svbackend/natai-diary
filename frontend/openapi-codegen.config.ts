import {generateReactQueryComponents, generateSchemaTypes,} from "@openapi-codegen/typescript";
import {defineConfig} from "@openapi-codegen/cli";

export default defineConfig({
    api: {
        from: {
            source: "url",
            url: "http://127.0.0.1:8000/api-docs.json",
        },
        outputDir: "src/api",
        to: async (context) => {
            const filenamePrefix = "api";
            const {schemasFiles} = await generateSchemaTypes(context, {
                filenamePrefix,
            });
            await generateReactQueryComponents(context, {
                filenamePrefix,
                schemasFiles,
            });
        },
    },
});

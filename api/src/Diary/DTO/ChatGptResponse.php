<?php

namespace App\Diary\DTO;

//{
//    "id": "chatcmpl-6uojGyqDI0dHE2ptEKiQYGrml3EYX",
//  "object": "chat.completion",
//  "created": 1678999390,
//  "model": "gpt-3.5-turbo-0301",
//  "usage": {
//    "prompt_tokens": 791,
//    "completion_tokens": 224,
//    "total_tokens": 1015
//  },
//  "choices": [
//    {
//        "message": {
//        "role": "assistant",
//        "content": ""
//      },
//      "finish_reason": "stop",
//      "index": 0
//    }
//  ]
//}
class ChatGptResponse
{
    public function __construct(
        public string $id,
        public string $object,
        public int $created,
        public string $model,
        public array $usage,
        public array $choices,
    )
    {
    }

    public function getFirstMessage()
    {
        return $this->choices[0]['message']['content'];
    }
}
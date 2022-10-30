<?php

namespace App\Diary\Security\Voter;

use App\Auth\Entity\User;
use App\Common\Security\BaseVoter;
use App\Diary\Entity\Note;

class NoteVoter extends BaseVoter
{
    public const EDIT = 'edit';
    public const DELETE = 'delete';

    private const ATTRIBUTES = [
        self::EDIT,
        self::DELETE,
    ];

    protected function supports(string $attribute, $subject): bool
    {
        return in_array($attribute, self::ATTRIBUTES) && $subject instanceof Note;
    }

    public function canDelete(Note $note, User $user): bool
    {
        $noteOwnerId = $note->getUser()->getId();
        $userId = $user->getId();

        return $noteOwnerId->equals($userId);
    }

    public function canEdit(Note $note, User $user): bool
    {
        $noteOwnerId = $note->getUser()->getId();
        $userId = $user->getId();

        return $noteOwnerId->equals($userId);
    }
}

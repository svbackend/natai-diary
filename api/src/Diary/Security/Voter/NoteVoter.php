<?php

namespace App\Diary\Security\Voter;

use App\Auth\Entity\User;
use App\Common\Security\BaseVoter;
use App\Diary\Entity\Note;

class NoteVoter extends BaseVoter
{
    public const DELETE = 'delete';

    protected function supports(string $attribute, $subject): bool
    {
        return in_array($attribute, [self::DELETE])
            && $subject instanceof Note;
    }

    protected function canDelete(Note $note, User $user): bool
    {
        $noteOwnerId = $note->getUser()->getId();
        $userId = $user->getId();

        return $noteOwnerId->equals($userId);
    }
}

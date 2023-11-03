<?php


namespace App\Common\Security;

use App\Auth\Entity\User;
use Symfony\Bundle\SecurityBundle\Security;
use Symfony\Component\Security\Core\Authentication\Token\TokenInterface;
use Symfony\Component\Security\Core\Authorization\Voter\Voter;

/**
 * @see https://symfony.com/doc/current/security/voters.html
 *
 * Extend this class to create a voter for a specific entity, e.g. NoteVoter
 *
 * you can use naming convention for attribute like "delete" to have a canDelete() method
 */
abstract class BaseVoter extends Voter
{
    public function __construct(
        protected Security $security
    )
    {
    }

    abstract protected function supports(string $attribute, $subject): bool;

    protected function voteOnAttribute($attribute, $subject, TokenInterface $token): bool
    {
        $user = $token->getUser();

        if (!$user instanceof User) {
            // the user must be logged in; if not, deny access
            return false;
        }
        $methodName = 'can' . ucfirst($attribute);

        return $this->{$methodName}($subject, $user);
    }
}
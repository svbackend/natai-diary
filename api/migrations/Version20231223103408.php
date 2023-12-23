<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20231223103408 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE city (id SERIAL NOT NULL, name VARCHAR(255) NOT NULL, google_place_id VARCHAR(255) NOT NULL, country VARCHAR(255) NOT NULL, popularity INT DEFAULT 0 NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_2D5B0234983C031 ON city (google_place_id)');
        $this->addSql('CREATE TABLE user_profile (id SERIAL NOT NULL, user_id UUID NOT NULL, city_id INT NOT NULL, timezone_offset INT NOT NULL, enable_email_notifications BOOLEAN DEFAULT true NOT NULL, PRIMARY KEY(id))');
        $this->addSql('CREATE UNIQUE INDEX UNIQ_D95AB405A76ED395 ON user_profile (user_id)');
        $this->addSql('CREATE INDEX IDX_D95AB4058BAC62AF ON user_profile (city_id)');
        $this->addSql('COMMENT ON COLUMN user_profile.user_id IS \'(DC2Type:uuid)\'');
        $this->addSql('ALTER TABLE user_profile ADD CONSTRAINT FK_D95AB405A76ED395 FOREIGN KEY (user_id) REFERENCES "users" (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE user_profile ADD CONSTRAINT FK_D95AB4058BAC62AF FOREIGN KEY (city_id) REFERENCES city (id) NOT DEFERRABLE INITIALLY IMMEDIATE');
        $this->addSql('ALTER TABLE messenger_messages ALTER created_at TYPE TIMESTAMP(0) WITHOUT TIME ZONE');
        $this->addSql('ALTER TABLE messenger_messages ALTER available_at TYPE TIMESTAMP(0) WITHOUT TIME ZONE');
        $this->addSql('ALTER TABLE messenger_messages ALTER delivered_at TYPE TIMESTAMP(0) WITHOUT TIME ZONE');
        $this->addSql('COMMENT ON COLUMN messenger_messages.created_at IS \'(DC2Type:datetime_immutable)\'');
        $this->addSql('COMMENT ON COLUMN messenger_messages.available_at IS \'(DC2Type:datetime_immutable)\'');
        $this->addSql('COMMENT ON COLUMN messenger_messages.delivered_at IS \'(DC2Type:datetime_immutable)\'');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE SCHEMA public');
        $this->addSql('ALTER TABLE user_profile DROP CONSTRAINT FK_D95AB405A76ED395');
        $this->addSql('ALTER TABLE user_profile DROP CONSTRAINT FK_D95AB4058BAC62AF');
        $this->addSql('DROP TABLE city');
        $this->addSql('DROP TABLE user_profile');
        $this->addSql('ALTER TABLE messenger_messages ALTER created_at TYPE TIMESTAMP(0) WITHOUT TIME ZONE');
        $this->addSql('ALTER TABLE messenger_messages ALTER available_at TYPE TIMESTAMP(0) WITHOUT TIME ZONE');
        $this->addSql('ALTER TABLE messenger_messages ALTER delivered_at TYPE TIMESTAMP(0) WITHOUT TIME ZONE');
        $this->addSql('COMMENT ON COLUMN messenger_messages.created_at IS NULL');
        $this->addSql('COMMENT ON COLUMN messenger_messages.available_at IS NULL');
        $this->addSql('COMMENT ON COLUMN messenger_messages.delivered_at IS NULL');
    }
}

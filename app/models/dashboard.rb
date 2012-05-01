class Dashboard < ActiveRecord::Base
  belongs_to :user
  belongs_to :dashtable, :polymorphic => true

  validates :name, :description, :user_id, :presence => true
end

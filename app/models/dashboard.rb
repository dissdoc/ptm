class Dashboard < ActiveRecord::Base
  belongs_to :user
  belongs_to :dashtable, :polymorphic => true

  has_many :notes, :as => :notable

  validates :name, :description, :user_id, :presence => true
end
